package com.gms.gmsmvp.util;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

public final class ExpiryThresholdParser {

    private static final int DEFAULT_MAX_DAYS = 30;

    private ExpiryThresholdParser() {
    }

    /**
     * Parses comma-separated positive integers (e.g. "3,7,15,30").
     * Invalid tokens ignored. Returns sorted unique ascending list.
     * If empty after parse, returns default [30].
     */
    public static List<Integer> parse(String raw) {
        TreeSet<Integer> values = new TreeSet<>();
        if (StringUtils.hasText(raw)) {
            for (String token : raw.split(",")) {
                String trimmed = token.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                try {
                    int days = Integer.parseInt(trimmed);
                    if (days > 0) {
                        values.add(days);
                    }
                } catch (NumberFormatException ignored) {
                    // skip invalid token
                }
            }
        }
        if (values.isEmpty()) {
            return List.of(DEFAULT_MAX_DAYS);
        }
        return Collections.unmodifiableList(new ArrayList<>(values));
    }

    public static int maxDays(List<Integer> thresholds) {
        if (thresholds == null || thresholds.isEmpty()) {
            return DEFAULT_MAX_DAYS;
        }
        return thresholds.get(thresholds.size() - 1);
    }

    /**
     * Smallest threshold &gt;= daysLeft (expiry alert bucket).
     */
    public static int nearestBucket(int daysLeft, List<Integer> thresholds) {
        List<Integer> sorted = thresholds == null || thresholds.isEmpty()
                ? List.of(DEFAULT_MAX_DAYS)
                : thresholds;
        for (Integer threshold : sorted) {
            if (threshold >= daysLeft) {
                return threshold;
            }
        }
        return sorted.get(sorted.size() - 1);
    }
}
