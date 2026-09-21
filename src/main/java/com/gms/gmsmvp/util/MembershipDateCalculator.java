package com.gms.gmsmvp.util;

import com.gms.gmsmvp.entity.DurationUnit;

import java.time.LocalDate;

public final class MembershipDateCalculator {

    private MembershipDateCalculator() {
    }

    /**
     * Inclusive end date: start + duration(unit) - 1 day.
     * Example: 2026-01-01 + 1 MONTH => 2026-01-31
     */
    public static LocalDate calculateEndDate(LocalDate startDate, int duration, DurationUnit unit) {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date is required");
        }
        if (duration < 1) {
            throw new IllegalArgumentException("Duration must be at least 1");
        }
        if (unit == null) {
            throw new IllegalArgumentException("Duration unit is required");
        }

        LocalDate afterPeriod = switch (unit) {
            case DAY -> startDate.plusDays(duration);
            case MONTH -> startDate.plusMonths(duration);
            case YEAR -> startDate.plusYears(duration);
        };
        return afterPeriod.minusDays(1);
    }
}
