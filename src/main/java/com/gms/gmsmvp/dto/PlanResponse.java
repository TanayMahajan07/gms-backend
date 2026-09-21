package com.gms.gmsmvp.dto;

import com.gms.gmsmvp.entity.DurationUnit;
import com.gms.gmsmvp.entity.MembershipPlan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponse {

    private Long id;
    private Long gymId;
    private String planName;
    private Integer duration;
    private DurationUnit durationUnit;
    private BigDecimal price;
    private String description;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PlanResponse from(MembershipPlan plan) {
        return new PlanResponse(
                plan.getId(),
                plan.getGymId(),
                plan.getPlanName(),
                plan.getDuration(),
                plan.getDurationUnit(),
                plan.getPrice(),
                plan.getDescription(),
                plan.getActive(),
                plan.getCreatedAt(),
                plan.getUpdatedAt()
        );
    }
}
