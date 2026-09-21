package com.gms.gmsmvp.service;

import com.gms.gmsmvp.dto.MemberRequest;
import com.gms.gmsmvp.dto.MemberResponse;
import com.gms.gmsmvp.entity.Member;
import com.gms.gmsmvp.entity.Users;
import com.gms.gmsmvp.repository.MemberRepository;
import com.gms.gmsmvp.exception.DuplicateResourceException;
import com.gms.gmsmvp.exception.ResourceNotFoundException;
import com.gms.gmsmvp.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private  Users user;

    private static final DateTimeFormatter MEMBER_CODE_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final MemberRepository memberRepository;

    public MemberResponse create(MemberRequest request) {

        user = SecurityUtil.getUserDetail();

        if (user == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        String memberCode = resolveMemberCode();
        if (memberRepository.existsByMemberCodeAndGymId(memberCode ,user.getGymId())) {
            throw new DuplicateResourceException("Member code already exists: " + memberCode);
        }



        Member member = new Member();

        member.setGymId(user.getGymId());
        member.setFirstName(request.getFirstName().trim());
        member.setLastName(request.getLastName().trim());
        member.setGender(request.getGender());
        member.setDateOfBirth(request.getDateOfBirth());
        member.setMobile(trimToNull(request.getMobile()));
        member.setEmail(trimToNull(request.getEmail()));
        member.setAddress(trimToNull(request.getAddress()));
        member.setCity(trimToNull(request.getCity()));
        member.setEmergencyContactName(trimToNull(request.getEmergencyContactName()));
        member.setEmergencyContactNumber(trimToNull(request.getEmergencyContactNumber()));
        member.setJoiningDate(request.getJoiningDate());
        member.setProfilePhoto(trimToNull(request.getProfilePhoto()));
        member.setCreatedBy(user.getId());
        member.setUpdatedBy(user.getId());
        if (request.getActive() != null) {
            member.setActive(request.getActive());
        }
        member.setRemarks(trimToNull(request.getRemarks()));
        member.setMemberCode(memberCode);

        member.setActive(request.getActive() == null || request.getActive());

        return MemberResponse.from(memberRepository.save(member));
    }

    @Transactional(readOnly = true)
    public Page<MemberResponse> search(String search, Boolean active, Pageable pageable) {
        String normalizedSearch = StringUtils.hasText(search) ? search.trim() : null;
        return memberRepository.search(normalizedSearch, SecurityUtil.getLoggedInGymId(), active, pageable).map(MemberResponse::from);
    }

    @Transactional(readOnly = true)
    public MemberResponse getById(Long id) {
        return MemberResponse.from(findMember(id));
    }

    @Transactional(readOnly = true)
    public MemberResponse getByCode(String memberCode) {

        return MemberResponse.from(memberRepository.findByMemberCodeAndGymId(memberCode,SecurityUtil.getLoggedInGymId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with code: " + memberCode)));
    }

    public MemberResponse update(Long id, MemberRequest request) {
        Member member = findMember(id);

        user = SecurityUtil.getUserDetail();

        if (user == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        //member.setGymId(user.getGymId());
        member.setFirstName(request.getFirstName().trim());
        member.setLastName(request.getLastName().trim());
        member.setGender(request.getGender());
        member.setDateOfBirth(request.getDateOfBirth());
        member.setMobile(trimToNull(request.getMobile()));
        member.setEmail(trimToNull(request.getEmail()));
        member.setAddress(trimToNull(request.getAddress()));
        member.setCity(trimToNull(request.getCity()));
        member.setEmergencyContactName(trimToNull(request.getEmergencyContactName()));
        member.setEmergencyContactNumber(trimToNull(request.getEmergencyContactNumber()));
        member.setJoiningDate(request.getJoiningDate());
        member.setProfilePhoto(trimToNull(request.getProfilePhoto()));
        member.setUpdatedBy(user.getId());
        if (request.getActive() != null) {
            member.setActive(request.getActive());
        }
        member.setRemarks(trimToNull(request.getRemarks()));

        member.setActive(request.getActive() == null || request.getActive());

        return MemberResponse.from(memberRepository.save(member));
    }

    public MemberResponse updateActive(Long id, boolean active) {

        Member member = findMember(id);
        member.setActive(active);
        member.setUpdatedBy(SecurityUtil.getLoggedInUserId());
        return MemberResponse.from(memberRepository.save(member));
    }

    public void deactivate(Long id, Long updatedBy) {
        Member member = findMember(id);
        member.setActive(false);
        member.setUpdatedBy(updatedBy);
        memberRepository.save(member);
    }

    private Member findMember(Long id) {
        return memberRepository.findByIdAndGymId(id ,SecurityUtil.getLoggedInGymId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
    }

    private String resolveMemberCode() {

        return "MEM-" + LocalDateTime.now().format(MEMBER_CODE_TIME);
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
