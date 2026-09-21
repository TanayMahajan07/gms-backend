package com.gms.gmsmvp.service;

import com.gms.gmsmvp.dto.PlanRequest;
import com.gms.gmsmvp.dto.PlanResponse;
import com.gms.gmsmvp.entity.MembershipPlan;
import com.gms.gmsmvp.exception.ResourceNotFoundException;
import com.gms.gmsmvp.repository.MembershipPlanRepository;
import com.gms.gmsmvp.security.GmsUserDetails;
import com.gms.gmsmvp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlanService {

    private final MembershipPlanRepository membershipPlanRepository;

    public PlanResponse create(PlanRequest request) {
        Long gymId = requireGymId();
        MembershipPlan plan = new MembershipPlan();
        plan.setGymId(gymId);
        apply(plan, request);
        plan.setActive(request.getActive() == null || request.getActive());
        return PlanResponse.from(membershipPlanRepository.save(plan));
    }

    @Transactional(readOnly = true)
    public Page<PlanResponse> search(String search, Boolean active, Pageable pageable) {
        Long gymId = requireGymId();
        String normalized = StringUtils.hasText(search) ? search.trim() : null;
        return membershipPlanRepository.search(gymId, normalized, active, pageable).map(PlanResponse::from);
    }

    @Transactional(readOnly = true)
    public List<PlanResponse> listActive() {
        Long gymId = requireGymId();
        return membershipPlanRepository.findByGymIdAndActiveTrueOrderByPlanNameAsc(gymId)
                .stream()
                .map(PlanResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PlanResponse getById(Long id) {
        return PlanResponse.from(findOwnedPlan(id));
    }

    public PlanResponse update(Long id, PlanRequest request) {
        MembershipPlan plan = findOwnedPlan(id);
        apply(plan, request);
        if (request.getActive() != null) {
            plan.setActive(request.getActive());
        }
        return PlanResponse.from(membershipPlanRepository.save(plan));
    }

    public PlanResponse updateActive(Long id, boolean active) {
        MembershipPlan plan = findOwnedPlan(id);
        plan.setActive(active);
        return PlanResponse.from(membershipPlanRepository.save(plan));
    }

    private MembershipPlan findOwnedPlan(Long id) {
        Long gymId = requireGymId();
        return membershipPlanRepository.findByIdAndGymId(id, gymId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + id));
    }

    private void apply(MembershipPlan plan, PlanRequest request) {
        plan.setPlanName(request.getPlanName().trim());
        plan.setDuration(request.getDuration());
        plan.setDurationUnit(request.getDurationUnit());
        plan.setPrice(request.getPrice());
        plan.setDescription(StringUtils.hasText(request.getDescription()) ? request.getDescription().trim() : null);
    }

    private Long requireGymId() {
        GmsUserDetails user = SecurityUtils.requireCurrentUser();
        if (user.getGymId() == null) {
            throw new AccessDeniedException("No gym is linked to this account");
        }
        return user.getGymId();
    }
}
