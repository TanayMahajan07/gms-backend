package com.gms.gmsmvp.service;

import com.gms.gmsmvp.repository.MembershipRepository;
import com.gms.gmsmvp.repository.MembershipStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembershipExpiryService {

    private static final String ACTIVE = "ACTIVE";
    private static final String EXPIRED = "EXPIRED";

    private final MembershipRepository membershipRepository;
    private final MembershipStatusRepository membershipStatusRepository;

    @Transactional
    public int expirePastDue() {
        Long expiredStatusId = membershipStatusRepository.findByStatusCode(EXPIRED)
                .orElseThrow(() -> new IllegalStateException(
                        "EXPIRED membership status is required in mst_membership_status"))
                .getId();

        LocalDate today = LocalDate.now();
        int updated = membershipRepository.expirePastDue(today, ACTIVE, EXPIRED, expiredStatusId);
        if (updated > 0) {
            log.info("Auto-expired {} membership(s) with end_date before {}", updated, today);
        }
        return updated;
    }
}
