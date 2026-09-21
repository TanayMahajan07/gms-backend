package com.gms.gmsmvp.scheduler;

import com.gms.gmsmvp.service.MembershipExpiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MembershipExpiryScheduler {

    private final MembershipExpiryService membershipExpiryService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Running membership auto-expire on application ready");
        membershipExpiryService.expirePastDue();
    }

    @Scheduled(cron = "0 5 0 * * *")
    public void expireDaily() {
        log.info("Running scheduled membership auto-expire");
        membershipExpiryService.expirePastDue();
    }
}
