package com.gms.gmsmvp.service;

import com.gms.gmsmvp.dto.MembershipPaymentSummaryResponse;
import com.gms.gmsmvp.dto.PaymentCreateRequest;
import com.gms.gmsmvp.dto.PaymentResponse;
import com.gms.gmsmvp.entity.GymSettings;
import com.gms.gmsmvp.entity.Member;
import com.gms.gmsmvp.entity.Membership;
import com.gms.gmsmvp.entity.MembershipPlan;
import com.gms.gmsmvp.entity.Payment;
import com.gms.gmsmvp.entity.PaymentStatus;
import com.gms.gmsmvp.exception.ResourceNotFoundException;
import com.gms.gmsmvp.repository.GymSettingsRepository;
import com.gms.gmsmvp.repository.MemberRepository;
import com.gms.gmsmvp.repository.MembershipPlanRepository;
import com.gms.gmsmvp.repository.MembershipRepository;
import com.gms.gmsmvp.repository.PaymentRepository;
import com.gms.gmsmvp.repository.PaymentStatusRepository;
import com.gms.gmsmvp.security.GmsUserDetails;
import com.gms.gmsmvp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private static final String PAID = "PAID";
    private static final String PARTIAL = "PARTIAL";
    private static final String DEFAULT_RECEIPT_PREFIX = "RCPT";
    private static final DateTimeFormatter REF_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final Set<String> ALLOWED_METHODS = Set.of(
            "CASH", "UPI", "CARD", "BANK_TRANSFER", "OTHER"
    );

    private final PaymentRepository paymentRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final MemberRepository memberRepository;
    private final GymSettingsRepository gymSettingsRepository;

    public PaymentResponse create(PaymentCreateRequest request) {
        GmsUserDetails currentUser = SecurityUtils.requireCurrentUser();
        Long gymId = requireGymId(currentUser);

        Membership membership = membershipRepository.findById(request.getMembershipId())
                .filter(m -> gymId.equals(m.getGymId()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Membership not found with id: " + request.getMembershipId()));

        String method = request.getPaymentMethod().trim().toUpperCase(Locale.ROOT);
        if (!ALLOWED_METHODS.contains(method)) {
            throw new IllegalArgumentException(
                    "Payment method must be one of: CASH, UPI, CARD, BANK_TRANSFER, OTHER");
        }

        BigDecimal amount = request.getAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal amountDue = membership.getAmount();
        BigDecimal amountPaid = paidSum(membership.getId());
        BigDecimal balance = amountDue.subtract(amountPaid);

        if (balance.compareTo(new BigDecimal("0.01")) < 0) {
            throw new IllegalArgumentException("Membership is already fully paid");
        }
        if (amount.compareTo(balance) > 0) {
            throw new IllegalArgumentException(
                    "Amount exceeds remaining balance of " + balance.setScale(2, RoundingMode.HALF_UP));
        }

        BigDecimal newPaid = amountPaid.add(amount);
        BigDecimal newBalance = amountDue.subtract(newPaid);
        String statusCode = newBalance.compareTo(BigDecimal.ZERO) <= 0 ? PAID : PARTIAL;

        PaymentStatus status = paymentStatusRepository.findByStatusCode(statusCode)
                .orElseThrow(() -> new IllegalStateException(
                        statusCode + " payment status is required in mst_payment_status"));

        Payment payment = new Payment();
        payment.setPaymentReference(resolvePaymentReference(gymId));
        payment.setGymId(gymId);
        payment.setMemberId(membership.getMemberId());
        payment.setMembershipId(membership.getId());
        payment.setAmount(amount);
        payment.setPaymentDate(request.getPaymentDate());
        payment.setPaymentMethod(method);
        payment.setPaymentStatus(statusCode);
        payment.setPaymentStatusId(status.getId());
        payment.setTransactionReference(trimToNull(request.getTransactionReference()));
        payment.setRemarks(trimToNull(request.getRemarks()));
        payment.setCreatedBy(currentUser.getId());

        Payment saved = paymentRepository.save(payment);
        return toResponse(saved, membership, amountDue, newPaid, newBalance.max(BigDecimal.ZERO));
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponse> search(String search, String status, Pageable pageable) {
        Long gymId = requireGymId(SecurityUtils.requireCurrentUser());
        String normalized = StringUtils.hasText(search) ? search.trim() : null;
        String statusFilter = StringUtils.hasText(status) ? status.trim().toUpperCase(Locale.ROOT) : null;

        return paymentRepository.search(gymId, normalized, statusFilter, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getById(Long id) {
        Long gymId = requireGymId(SecurityUtils.requireCurrentUser());
        Payment payment = paymentRepository.findById(id)
                .filter(p -> gymId.equals(p.getGymId()))
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return toResponse(payment);
    }

    @Transactional(readOnly = true)
    public MembershipPaymentSummaryResponse summaryForMembership(Long membershipId) {
        Long gymId = requireGymId(SecurityUtils.requireCurrentUser());
        Membership membership = membershipRepository.findById(membershipId)
                .filter(m -> gymId.equals(m.getGymId()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Membership not found with id: " + membershipId));

        Member member = memberRepository.findById(membership.getMemberId()).orElse(null);
        MembershipPlan plan = membershipPlanRepository.findById(membership.getPlanId()).orElse(null);
        BigDecimal amountPaid = paidSum(membership.getId());
        BigDecimal amountDue = membership.getAmount();
        BigDecimal balance = amountDue.subtract(amountPaid);
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            balance = BigDecimal.ZERO;
        }

        return new MembershipPaymentSummaryResponse(
                membership.getId(),
                membership.getMembershipCode(),
                membership.getMemberId(),
                member != null ? member.getMemberCode() : null,
                memberDisplayName(member),
                plan != null ? plan.getPlanName() : null,
                amountDue,
                amountPaid,
                balance
        );
    }

    private PaymentResponse toResponse(Payment payment) {
        Membership membership = membershipRepository.findById(payment.getMembershipId()).orElse(null);
        BigDecimal amountDue = membership != null ? membership.getAmount() : null;
        BigDecimal amountPaid = paidSum(payment.getMembershipId());
        BigDecimal balance = amountDue != null
                ? amountDue.subtract(amountPaid).max(BigDecimal.ZERO)
                : null;
        return toResponse(payment, membership, amountDue, amountPaid, balance);
    }

    private PaymentResponse toResponse(Payment payment,
                                       Membership membership,
                                       BigDecimal amountDue,
                                       BigDecimal amountPaid,
                                       BigDecimal balance) {
        Member member = memberRepository.findById(payment.getMemberId()).orElse(null);
        String membershipCode = membership != null ? membership.getMembershipCode() : null;
        return PaymentResponse.from(
                payment,
                member != null ? member.getMemberCode() : null,
                memberDisplayName(member),
                membershipCode,
                amountDue,
                amountPaid,
                balance
        );
    }

    private BigDecimal paidSum(Long membershipId) {
        BigDecimal sum = paymentRepository.sumPaidForMembership(membershipId);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    private String resolvePaymentReference(Long gymId) {
        String prefix = gymSettingsRepository.findByGymId(gymId)
                .map(GymSettings::getReceiptPrefix)
                .filter(StringUtils::hasText)
                .orElse(DEFAULT_RECEIPT_PREFIX)
                .trim();

        String reference;
        do {
            reference = prefix + "-" + LocalDateTime.now().format(REF_TIME);
        } while (paymentRepository.existsByPaymentReference(reference));
        return reference;
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

    private static String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private Long requireGymId(GmsUserDetails user) {
        if (user.getGymId() == null) {
            throw new AccessDeniedException("No gym is linked to this account");
        }
        return user.getGymId();
    }
}
