package com.gms.gmsmvp.service;

import com.gms.gmsmvp.dto.ReportResponse;
import com.gms.gmsmvp.dto.ReportResponse.DayTotal;
import com.gms.gmsmvp.dto.ReportResponse.MembershipSnapshot;
import com.gms.gmsmvp.dto.ReportResponse.MethodTotal;
import com.gms.gmsmvp.dto.ReportResponse.OutstandingItem;
import com.gms.gmsmvp.entity.GymSettings;
import com.gms.gmsmvp.entity.Member;
import com.gms.gmsmvp.entity.Membership;
import com.gms.gmsmvp.repository.GymSettingsRepository;
import com.gms.gmsmvp.repository.MemberRepository;
import com.gms.gmsmvp.repository.MembershipRepository;
import com.gms.gmsmvp.repository.PaymentRepository;
import com.gms.gmsmvp.security.GmsUserDetails;
import com.gms.gmsmvp.security.SecurityUtils;
import com.gms.gmsmvp.util.ExpiryThresholdParser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private static final String ACTIVE = "ACTIVE";
    private static final int OUTSTANDING_LIMIT = 50;

    private final PaymentRepository paymentRepository;
    private final MembershipRepository membershipRepository;
    private final MemberRepository memberRepository;
    private final GymSettingsRepository gymSettingsRepository;

    public ReportResponse getReport(LocalDate from, LocalDate to) {
        Long gymId = requireGymId(SecurityUtils.requireCurrentUser());
        LocalDate today = LocalDate.now();

        LocalDate fromDate = from != null ? from : today.withDayOfMonth(1);
        LocalDate toDate = to != null ? to : today;
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("From date must be on or before to date");
        }

        BigDecimal collectionTotal = paymentRepository.sumCollected(gymId, fromDate, toDate);
        if (collectionTotal == null) {
            collectionTotal = BigDecimal.ZERO;
        }

        List<MethodTotal> byMethod = new ArrayList<>();
        for (Object[] row : paymentRepository.sumCollectedByMethod(gymId, fromDate, toDate)) {
            byMethod.add(new MethodTotal(
                    (String) row[0],
                    (BigDecimal) row[1],
                    ((Number) row[2]).longValue()
            ));
        }

        List<DayTotal> byDay = new ArrayList<>();
        for (Object[] row : paymentRepository.sumCollectedByDay(gymId, fromDate, toDate)) {
            byDay.add(new DayTotal(
                    (LocalDate) row[0],
                    (BigDecimal) row[1],
                    ((Number) row[2]).longValue()
            ));
        }

        BigDecimal outstandingTotal = membershipRepository.sumOutstandingAmount(gymId);
        if (outstandingTotal == null) {
            outstandingTotal = BigDecimal.ZERO;
        }
        long outstandingCount = membershipRepository.countOutstanding(gymId);

        List<OutstandingItem> outstanding = new ArrayList<>();
        for (Membership membership : membershipRepository.findOutstanding(
                gymId, PageRequest.of(0, OUTSTANDING_LIMIT))) {
            BigDecimal paid = paymentRepository.sumPaidForMembership(membership.getId());
            if (paid == null) {
                paid = BigDecimal.ZERO;
            }
            BigDecimal balance = membership.getAmount().subtract(paid);
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                balance = BigDecimal.ZERO;
            }
            Member member = memberRepository.findById(membership.getMemberId()).orElse(null);
            outstanding.add(new OutstandingItem(
                    membership.getId(),
                    membership.getMembershipCode(),
                    member != null ? member.getMemberCode() : null,
                    memberDisplayName(member),
                    membership.getAmount(),
                    paid,
                    balance
            ));
        }

        List<Integer> thresholds = ExpiryThresholdParser.parse(
                gymSettingsRepository.findByGymId(gymId)
                        .map(GymSettings::getExpiryAlertThresholds)
                        .orElse(null));
        int horizonDays = ExpiryThresholdParser.maxDays(thresholds);
        LocalDate horizon = today.plusDays(horizonDays);

        MembershipSnapshot snapshot = new MembershipSnapshot(
                membershipRepository.countByGymIdAndMembershipStatus(gymId, ACTIVE),
                membershipRepository.countExpiringSoon(gymId, today, horizon),
                membershipRepository.countExpiredNeedingRenew(gymId),
                horizonDays
        );

        return new ReportResponse(
                fromDate,
                toDate,
                collectionTotal,
                byMethod,
                byDay,
                outstandingTotal,
                outstandingCount,
                outstanding,
                snapshot
        );
    }

    private static String memberDisplayName(Member member) {
        if (member == null) {
            return null;
        }
        if (member.getLastName() == null || member.getLastName().isBlank()) {
            return member.getFirstName();
        }
        return member.getFirstName() + " " + member.getLastName();
    }

    private Long requireGymId(GmsUserDetails user) {
        if (user.getGymId() == null) {
            throw new AccessDeniedException("No gym is linked to this account");
        }
        return user.getGymId();
    }
}
