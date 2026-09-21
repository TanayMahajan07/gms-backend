package com.gms.gmsmvp.service;

import com.gms.gmsmvp.dto.DashboardResponse;
import com.gms.gmsmvp.dto.DashboardResponse.DashboardMembershipItem;
import com.gms.gmsmvp.dto.DashboardResponse.DashboardSummary;
import com.gms.gmsmvp.entity.GymSettings;
import com.gms.gmsmvp.entity.Member;
import com.gms.gmsmvp.entity.Membership;
import com.gms.gmsmvp.entity.MembershipPlan;
import com.gms.gmsmvp.repository.GymSettingsRepository;
import com.gms.gmsmvp.repository.MemberRepository;
import com.gms.gmsmvp.repository.MembershipPlanRepository;
import com.gms.gmsmvp.repository.MembershipRepository;
import com.gms.gmsmvp.security.GmsUserDetails;
import com.gms.gmsmvp.security.SecurityUtils;
import com.gms.gmsmvp.util.ExpiryThresholdParser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private static final String ACTIVE = "ACTIVE";
    private static final int LIST_LIMIT = 50;

    private final MembershipRepository membershipRepository;
    private final MemberRepository memberRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final GymSettingsRepository gymSettingsRepository;

    public DashboardResponse getDashboard() {
        Long gymId = requireGymId(SecurityUtils.requireCurrentUser());
        LocalDate today = LocalDate.now();

        List<Integer> thresholds = ExpiryThresholdParser.parse(
                gymSettingsRepository.findByGymId(gymId)
                        .map(GymSettings::getExpiryAlertThresholds)
                        .orElse(null));
        int horizonDays = ExpiryThresholdParser.maxDays(thresholds);
        LocalDate horizon = today.plusDays(horizonDays);

        PageRequest listPage = PageRequest.of(0, LIST_LIMIT);

        BigDecimal outstandingAmount = membershipRepository.sumOutstandingAmount(gymId);
        if (outstandingAmount == null) {
            outstandingAmount = BigDecimal.ZERO;
        }

        DashboardSummary summary = new DashboardSummary(
                membershipRepository.countByGymIdAndMembershipStatus(gymId, ACTIVE),
                membershipRepository.countExpiringSoon(gymId, today, horizon),
                membershipRepository.countExpiredNeedingRenew(gymId),
                membershipRepository.countOutstanding(gymId),
                outstandingAmount,
                horizonDays
        );

        List<DashboardMembershipItem> expiringSoon = membershipRepository
                .findExpiringSoon(gymId, today, horizon, listPage)
                .stream()
                .map(m -> toExpiringItem(m, today, thresholds))
                .toList();

        List<DashboardMembershipItem> expiredNeedingRenew = membershipRepository
                .findExpiredNeedingRenew(gymId, listPage)
                .stream()
                .map(this::toExpiredItem)
                .toList();

        return new DashboardResponse(summary, expiringSoon, expiredNeedingRenew);
    }

    private DashboardMembershipItem toExpiringItem(Membership membership,
                                                   LocalDate today,
                                                   List<Integer> thresholds) {
        int daysLeft = (int) ChronoUnit.DAYS.between(today, membership.getEndDate());
        int bucket = ExpiryThresholdParser.nearestBucket(daysLeft, thresholds);
        return toItem(membership, daysLeft, bucket);
    }

    private DashboardMembershipItem toExpiredItem(Membership membership) {
        return toItem(membership, null, null);
    }

    private DashboardMembershipItem toItem(Membership membership, Integer daysLeft, Integer bucket) {
        Member member = memberRepository.findById(membership.getMemberId()).orElse(null);
        MembershipPlan plan = membershipPlanRepository.findById(membership.getPlanId()).orElse(null);
        return new DashboardMembershipItem(
                membership.getId(),
                membership.getMembershipCode(),
                membership.getMemberId(),
                member != null ? member.getMemberCode() : null,
                memberDisplayName(member),
                plan != null ? plan.getPlanName() : null,
                membership.getStartDate(),
                membership.getEndDate(),
                daysLeft,
                bucket,
                membership.getMembershipStatus()
        );
    }

    private static String memberDisplayName(Member member) {
        if (member == null) {
            return null;
        }
        if (member.getLastName() == null || member.getLastName().isBlank()) {
            return member.getFirstName();
        }
        return member.getFirstName() + " " + member.getLastName();
    }

    private Long requireGymId(GmsUserDetails user) {
        if (user.getGymId() == null) {
            throw new AccessDeniedException("No gym is linked to this account");
        }
        return user.getGymId();
    }
}
