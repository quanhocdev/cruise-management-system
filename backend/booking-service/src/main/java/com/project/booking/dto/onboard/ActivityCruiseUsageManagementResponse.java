package com.project.booking.dto.onboard;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityCruiseUsageManagementResponse(
        Long id,
        Long bookingPassengerId,
        String passengerName,
        String bookingCode,

        UUID activityCruiseTourId,
        String activityName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer maxPassengers,
        BigDecimal price,
        String activityCruiseTourStatus,

        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal discountAmount,
        BigDecimal finalAmount,
        LocalDateTime usedAt) {
}