package com.project.payment.dto.refund;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class RefundModels {

    private RefundModels() {
    }

    public record Request(
            @NotNull UUID clientRequestId,

            @NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2) BigDecimal amount,

            @NotBlank @Size(max = 1000) String reason) {
    }

    public record Receipt(
            UUID refundId,
            Long paymentId,
            BigDecimal amount,
            String status,
            Instant createdAt) {
    }

    public record Accepted(
            Receipt receipt,
            boolean replay) {
    }

    public record Refund(
            UUID id,
            Long paymentId,
            UUID actorId,
            BigDecimal amount,
            String reason,
            String status,
            String transactionType,
            String originalTransactionNo,
            Instant originalCreatedAt,
            String providerTransactionNo,
            String providerCode,
            Instant createdAt,
            Instant updatedAt) {
        public String orderInfo() {
            return "Refund " + id.toString().replace("-", "");
        }
    }

    public record Summary(
            Long paymentId,
            BigDecimal paidAmount,
            BigDecimal refundedAmount,
            BigDecimal pendingAmount,
            BigDecimal refundableAmount,
            boolean canRefund,
            String blockedReason,
            List<Refund> refunds,
            int page,
            int size) {
    }

    public record Outcome(
            String status,
            String code,
            String transactionNo) {
        public static Outcome unknown(String code) {
            return new Outcome("UNKNOWN", code, null);
        }
    }
}