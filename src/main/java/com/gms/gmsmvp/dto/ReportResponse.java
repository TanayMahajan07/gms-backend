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
public class ReportResponse {

    private LocalDate from;
    private LocalDate to;
    private BigDecimal collectionTotal = BigDecimal.ZERO;
    private List<MethodTotal> byMethod = new ArrayList<>();
    private List<DayTotal> byDay = new ArrayList<>();
    private BigDecimal outstandingTotal = BigDecimal.ZERO;
    private long outstandingCount;
    private List<OutstandingItem> outstanding = new ArrayList<>();
    private MembershipSnapshot membershipSnapshot = new MembershipSnapshot();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MethodTotal {
        private String method;
        private BigDecimal total;
        private long count;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayTotal {
        private LocalDate date;
        private BigDecimal total;
        private long count;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OutstandingItem {
        private Long membershipId;
        private String membershipCode;
        private String memberCode;
        private String memberName;
        private BigDecimal amountDue;
        private BigDecimal amountPaid;
        private BigDecimal balance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MembershipSnapshot {
        private long activeCount;
        private long expiringSoonCount;
        private long expiredNeedingRenewCount;
        private int expiryHorizonDays;
    }
}
