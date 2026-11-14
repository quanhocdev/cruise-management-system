package com.project.common.event;

import java.math.BigDecimal;
import java.util.UUID;

public record RefundApprovedEvent(
        Long bookingRefundId,
        Long bookingId,
        UUID paymentClientRequestId,
        BigDecimal amount,
        String reason,
        UUID actorId) {
}