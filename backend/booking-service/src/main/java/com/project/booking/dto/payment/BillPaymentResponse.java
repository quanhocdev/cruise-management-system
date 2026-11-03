
package com.project.booking.dto.payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BillPaymentResponse(
                Long paymentId,
                Long billId,
                UUID payerId,
                BigDecimal amount,
                String method,
                String status,
                String transactionCode,
                String paymentUrl,
                Instant createdAt,
                Instant expiresAt) {
}
