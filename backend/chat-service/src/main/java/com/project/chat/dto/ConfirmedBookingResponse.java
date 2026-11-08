package com.project.chat.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ConfirmedBookingResponse(
        Long bookingId,
        String bookingCode,
        String recipientName,
        Integer numberPassengers,
        BigDecimal totalAmount,
        Instant confirmedAt) {
}