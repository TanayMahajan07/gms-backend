package com.gms.gmsmvp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipCreateRequest {

    @NotNull(message = "Member is required")
    private Long memberId;

    @NotNull(message = "Plan is required")
    private Long planId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
}
