package com.gms.gmsmvp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private DashboardSummary summary = new DashboardSummary();
    private List<DashboardMembershipItem> expiringSoon = new ArrayList<>();
    private List<DashboardMembershipItem> expiredNeedingRenew = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardSummary {
        private long activeCount;
        private long expiringSoonCount;
        private long expiredNeedingRenewCount;
        private long outstandingCount;
        private BigDecimal outstandingAmount = BigDecimal.ZERO;
        private int expiryHorizonDays;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardMembershipItem {
        private Long membershipId;
        private String membershipCode;
        private Long memberId;
        private String memberCode;
        private String memberName;
        private String planName;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer daysLeft;
        private Integer nearestThresholdBucket;
        private String membershipStatus;
    }
}
