package com.project.common.event;

import java.time.Instant;
import java.util.UUID;

public record BillPaymentSuccessEvent(
                Long billId,
                Long paymentId,
                UUID userId,
                String status,
                Instant paidAt) {
}