package com.project.payment.dto;

import com.project.payment.model.enums.PaymentMethod;
import com.project.payment.model.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record BillPaymentResponse(
        Long paymentId,
        Long billId,
        Long payerId,
        BigDecimal amount,
        PaymentMethod method,
        PaymentStatus status,
        String transactionCode,
        String paymentUrl,
        Instant createdAt,
        Instant expiresAt) {
}