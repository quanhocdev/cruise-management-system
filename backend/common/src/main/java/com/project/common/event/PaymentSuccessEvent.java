package com.project.common.event;

import java.time.Instant;

public record PaymentSuccessEvent(
        Long bookingId,
        Long paymentId,
        Long userId,
        String status,
        Instant paidAt) {
}