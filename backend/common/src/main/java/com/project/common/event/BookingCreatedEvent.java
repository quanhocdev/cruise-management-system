package com.project.common.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookingCreatedEvent(
        Long bookingId,
        Long userId,
        UUID tourId,
        UUID tourPackageId,
        int passengerCount,
        BigDecimal totalPrice,
        LocalDateTime createdAt) {
}