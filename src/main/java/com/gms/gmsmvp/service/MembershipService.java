package com.gms.gmsmvp.service;

import com.gms.gmsmvp.dto.MembershipCreateRequest;
import com.gms.gmsmvp.dto.MembershipRenewRequest;
import com.gms.gmsmvp.dto.MembershipResponse;
import com.gms.gmsmvp.entity.Member;
import com.gms.gmsmvp.entity.Membership;
import com.gms.gmsmvp.entity.MembershipPlan;
import com.gms.gmsmvp.entity.MembershipStatus;
import com.gms.gmsmvp.exception.ResourceNotFoundException;
import com.gms.gmsmvp.repository.MemberRepository;
import com.gms.gmsmvp.repository.MembershipPlanRepository;
import com.gms.gmsmvp.repository.MembershipRepository;
import com.gms.gmsmvp.repository.MembershipStatusRepository;
import com.gms.gmsmvp.security.GmsUserDetails;
import com.gms.gmsmvp.security.SecurityUtils;
import com.gms.gmsmvp.exception.DuplicateResourceException;
import com.gms.gmsmvp.util.MembershipDateCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class MembershipService {

    private static final String ACTIVE = "ACTIVE";
    private static final String EXPIRED = "EXPIRED";
    private static final Set<String> RENEWABLE_STATUSES = Set.of(ACTIVE, EXPIRED);
    private static final DateTimeFormatter CODE_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final MembershipRepository membershipRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final MemberRepository memberRepository;
    private final MembershipStatusRepository membershipStatusRepository;

    public MembershipResponse create(MembershipCreateRequest request) {
        GmsUserDetails currentUser = SecurityUtils.requireCurrentUser();
        Long gymId = requireGymId(currentUser);

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + request.getMemberId()));
        if (!gymId.equals(member.getGymId())) {
            throw new AccessDeniedException("Member does not belong to your gym");
        }
        if (Boolean.FALSE.equals(member.getActive())) {
            throw new IllegalArgumentException("Cannot create membership for an inactive member");
        }

        MembershipPlan plan = membershipPlanRepository.findByIdAndGymId(request.getPlanId(), gymId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + request.getPlanId()));
        if (!Boolean.TRUE.equals(plan.getActive())) {
            throw new IllegalArgumentException("Selected plan is inactive");
        }

        if (membershipRepository.existsByMemberIdAndGymIdAndMembershipStatus(member.getId(), gymId, ACTIVE)) {
            throw new DuplicateResourceException(
                    "Member already has an ACTIVE membership. Use Renew on the membership list.");
        }

        BigDecimal amount = request.getAmount() != null ? request.getAmount() : plan.getPrice();
        if (amount == null || amount.compareTo(new BigDecimal("0.01")) < 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        MembershipStatus activeStatus = membershipStatusRepository.findByStatusCode(ACTIVE)
                .orElseThrow(() -> new IllegalStateException("ACTIVE membership status is required in mst_membership_status"));

        Membership membership = new Membership();
        membership.setMembershipCode(resolveMembershipCode());
        membership.setGymId(gymId);
        membership.setMemberId(member.getId());
        membership.setPlanId(plan.getId());
        membership.setStartDate(request.getStartDate());
        membership.setEndDate(MembershipDateCalculator.calculateEndDate(
                request.getStartDate(), plan.getDuration(), plan.getDurationUnit()));
        membership.setAmount(amount);
        membership.setMembershipStatus(ACTIVE);
        membership.setMembershipStatusId(activeStatus.getId());
        membership.setCreatedBy(currentUser.getId());

        Membership saved = membershipRepository.save(membership);
        return toResponse(saved, member, plan);
    }

    public MembershipResponse renew(Long sourceMembershipId, MembershipRenewRequest request) {
        GmsUserDetails currentUser = SecurityUtils.requireCurrentUser();
        Long gymId = requireGymId(currentUser);

        Membership source = membershipRepository.findById(sourceMembershipId)
                .filter(m -> gymId.equals(m.getGymId()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Membership not found with id: " + sourceMembershipId));

        if (!RENEWABLE_STATUSES.contains(source.getMembershipStatus())) {
            throw new IllegalArgumentException(
                    "Only ACTIVE or EXPIRED memberships can be renewed");
        }

        Member member = memberRepository.findById(source.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Member not found with id: " + source.getMemberId()));
        if (Boolean.FALSE.equals(member.getActive())) {
            throw new IllegalArgumentException("Cannot renew membership for an inactive member");
        }

        MembershipPlan plan = membershipPlanRepository.findByIdAndGymId(request.getPlanId(), gymId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + request.getPlanId()));
        if (!Boolean.TRUE.equals(plan.getActive())) {
            throw new IllegalArgumentException("Selected plan is inactive");
        }

        BigDecimal amount = request.getAmount() != null ? request.getAmount() : plan.getPrice();
        if (amount == null || amount.compareTo(new BigDecimal("0.01")) < 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        MembershipStatus expiredStatus = membershipStatusRepository.findByStatusCode(EXPIRED)
                .orElseThrow(() -> new IllegalStateException(
                        "EXPIRED membership status is required in mst_membership_status"));
        MembershipStatus activeStatus = membershipStatusRepository.findByStatusCode(ACTIVE)
                .orElseThrow(() -> new IllegalStateException(
                        "ACTIVE membership status is required in mst_membership_status"));

        if (ACTIVE.equals(source.getMembershipStatus())) {
            source.setMembershipStatus(EXPIRED);
            source.setMembershipStatusId(expiredStatus.getId());
            membershipRepository.save(source);
        }

        LocalDate startDate = resolveRenewStartDate(source.getEndDate());
        LocalDate endDate = MembershipDateCalculator.calculateEndDate(
                startDate, plan.getDuration(), plan.getDurationUnit());

        Membership renewed = new Membership();
        renewed.setMembershipCode(resolveMembershipCode());
        renewed.setGymId(gymId);
        renewed.setMemberId(member.getId());
        renewed.setPlanId(plan.getId());
        renewed.setStartDate(startDate);
        renewed.setEndDate(endDate);
        renewed.setAmount(amount);
        renewed.setMembershipStatus(ACTIVE);
        renewed.setMembershipStatusId(activeStatus.getId());
        renewed.setCreatedBy(currentUser.getId());

        Membership saved = membershipRepository.save(renewed);
        return toResponse(saved, member, plan);
    }

    static LocalDate resolveRenewStartDate(LocalDate sourceEndDate) {
        LocalDate today = LocalDate.now();
        if (sourceEndDate != null && !sourceEndDate.isBefore(today)) {
            return sourceEndDate.plusDays(1);
        }
        return today;
    }

    @Transactional(readOnly = true)
    public Page<MembershipResponse> search(String search, String status, Pageable pageable) {
        Long gymId = requireGymId(SecurityUtils.requireCurrentUser());
        String normalized = StringUtils.hasText(search) ? search.trim() : null;
        String statusFilter = StringUtils.hasText(status) ? status.trim().toUpperCase() : null;

        return membershipRepository.search(gymId, normalized, statusFilter, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public MembershipResponse getById(Long id) {
        Long gymId = requireGymId(SecurityUtils.requireCurrentUser());
        Membership membership = membershipRepository.findById(id)
                .filter(m -> gymId.equals(m.getGymId()))
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found with id: " + id));
        return toResponse(membership);
    }

    private MembershipResponse toResponse(Membership membership) {
        Member member = memberRepository.findById(membership.getMemberId()).orElse(null);
        MembershipPlan plan = membershipPlanRepository.findById(membership.getPlanId()).orElse(null);
        return toResponse(membership, member, plan);
    }

    private MembershipResponse toResponse(Membership membership, Member member, MembershipPlan plan) {
        String memberCode = member != null ? member.getMemberCode() : null;
        String memberName = member != null
                ? (member.getLastName() == null || member.getLastName().isBlank()
                ? member.getFirstName()
                : member.getFirstName() + " " + member.getLastName())
                : null;
        String planName = plan != null ? plan.getPlanName() : null;
        return MembershipResponse.from(membership, memberCode, memberName, planName);
    }

    private String resolveMembershipCode() {
        String code;
        do {
            code = "MS-" + LocalDateTime.now().format(CODE_TIME);
        } while (membershipRepository.existsByMembershipCode(code));
        return code;
    }

    private Long requireGymId(GmsUserDetails user) {
        if (user.getGymId() == null) {
            throw new AccessDeniedException("No gym is linked to this account");
        }
        return user.getGymId();
    }
}
