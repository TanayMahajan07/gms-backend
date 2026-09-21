package com.gms.gmsmvp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipPaymentSummaryResponse {

    private Long membershipId;
    private String membershipCode;
    private Long memberId;
    private String memberCode;
    private String memberName;
    private String planName;
    private BigDecimal amountDue;
    private BigDecimal amountPaid;
    private BigDecimal balance;
}
