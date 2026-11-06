package com.project.common.event;

import java.math.BigDecimal;
import java.util.UUID;

public record BookingConfirmedEvent(
        Long bookingId,
        UUID recipientUserId,
        String recipientEmail,
        String recipientName,
        String bookingCode,
        Integer numberPassengers,
        BigDecimal totalAmount) {
}