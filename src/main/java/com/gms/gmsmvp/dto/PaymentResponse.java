package com.gms.gmsmvp.dto;

import com.gms.gmsmvp.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private String paymentReference;
    private Long gymId;
    private Long memberId;
    private String memberCode;
    private String memberName;
    private Long membershipId;
    private String membershipCode;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String paymentStatus;
    private Long paymentStatusId;
    private String transactionReference;
    private String remarks;
    private LocalDateTime createdAt;
    private BigDecimal amountDue;
    private BigDecimal amountPaid;
    private BigDecimal balance;

    public static PaymentResponse from(Payment payment,
                                       String memberCode,
                                       String memberName,
                                       String membershipCode,
                                       BigDecimal amountDue,
                                       BigDecimal amountPaid,
                                       BigDecimal balance) {
        return new PaymentResponse(
                payment.getId(),
                payment.getPaymentReference(),
                payment.getGymId(),
                payment.getMemberId(),
                memberCode,
                memberName,
                payment.getMembershipId(),
                membershipCode,
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getPaymentStatusId(),
                payment.getTransactionReference(),
                payment.getRemarks(),
                payment.getCreatedAt(),
                amountDue,
                amountPaid,
                balance
        );
    }
}
