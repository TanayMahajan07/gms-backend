package com.gms.gmsmvp.service;

import com.gms.gmsmvp.dto.MemberRequest;
import com.gms.gmsmvp.dto.MemberResponse;
import com.gms.gmsmvp.entity.Member;
import com.gms.gmsmvp.repository.MemberRepository;
import com.gms.gmsmvp.shared.DuplicateResourceException;
import com.gms.gmsmvp.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private static final DateTimeFormatter MEMBER_CODE_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final MemberRepository memberRepository;

    public MemberResponse create(MemberRequest request) {
        String memberCode = resolveMemberCode(request.getMemberCode());
        if (memberRepository.existsByMemberCode(memberCode)) {
            throw new DuplicateResourceException("Member code already exists: " + memberCode);
        }

        Member member = new Member();
        member.setMemberCode(memberCode);
        apply(member, request);
        member.setActive(request.getActive() == null || request.getActive());
        member.setCreatedBy(request.getCreatedBy());
        member.setUpdatedBy(request.getUpdatedBy());

        return MemberResponse.from(memberRepository.save(member));
    }

    @Transactional(readOnly = true)
    public Page<MemberResponse> search(String search, Long gymId, Boolean active, Pageable pageable) {
        String normalizedSearch = StringUtils.hasText(search) ? search.trim() : null;
        return memberRepository.search(normalizedSearch, gymId, active, pageable).map(MemberResponse::from);
    }

    @Transactional(readOnly = true)
    public MemberResponse getById(Long id) {
        return MemberResponse.from(findMember(id));
    }

    @Transactional(readOnly = true)
    public MemberResponse getByCode(String memberCode) {
        return MemberResponse.from(memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with code: " + memberCode)));
    }

    public MemberResponse update(Long id, MemberRequest request) {
        Member member = findMember(id);

        if (StringUtils.hasText(request.getMemberCode()) && !request.getMemberCode().equals(member.getMemberCode())) {
            if (memberRepository.existsByMemberCode(request.getMemberCode())) {
                throw new DuplicateResourceException("Member code already exists: " + request.getMemberCode());
            }
            member.setMemberCode(request.getMemberCode().trim());
        }

        apply(member, request);
        member.setUpdatedBy(request.getUpdatedBy());

        return MemberResponse.from(memberRepository.save(member));
    }

    public MemberResponse updateActive(Long id, boolean active, Long updatedBy) {
        Member member = findMember(id);
        member.setActive(active);
        member.setUpdatedBy(updatedBy);
        return MemberResponse.from(memberRepository.save(member));
    }

    public void deactivate(Long id, Long updatedBy) {
        Member member = findMember(id);
        member.setActive(false);
        member.setUpdatedBy(updatedBy);
        memberRepository.save(member);
    }

    private Member findMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
    }

    private void apply(Member member, MemberRequest request) {
        member.setGymId(request.getGymId());
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
        if (request.getActive() != null) {
            member.setActive(request.getActive());
        }
        member.setRemarks(trimToNull(request.getRemarks()));
    }

    private String resolveMemberCode(String memberCode) {
        if (StringUtils.hasText(memberCode)) {
            return memberCode.trim();
        }
        return "MEM-" + LocalDateTime.now().format(MEMBER_CODE_TIME);
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
