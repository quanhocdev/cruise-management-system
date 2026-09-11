package com.project.common.event;

import java.math.BigDecimal;

public record BookingConfirmedEvent(
        Long recipientUserId,
        String recipientEmail,
        String recipientName,
        String bookingCode,
        Integer numberPassengers,
        BigDecimal totalAmount) {
}