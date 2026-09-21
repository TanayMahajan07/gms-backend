package com.gms.gmsmvp.dto;

import com.gms.gmsmvp.entity.Membership;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipResponse {

    private Long id;
    private String membershipCode;
    private Long gymId;
    private Long memberId;
    private String memberCode;
    private String memberName;
    private Long planId;
    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal amount;
    private String membershipStatus;
    private Long membershipStatusId;
    private LocalDateTime createdAt;

    public static MembershipResponse from(Membership membership,
                                          String memberCode,
                                          String memberName,
                                          String planName) {
        return new MembershipResponse(
                membership.getId(),
                membership.getMembershipCode(),
                membership.getGymId(),
                membership.getMemberId(),
                memberCode,
                memberName,
                membership.getPlanId(),
                planName,
                membership.getStartDate(),
                membership.getEndDate(),
                membership.getAmount(),
                membership.getMembershipStatus(),
                membership.getMembershipStatusId(),
                membership.getCreatedAt()
        );
    }
}
