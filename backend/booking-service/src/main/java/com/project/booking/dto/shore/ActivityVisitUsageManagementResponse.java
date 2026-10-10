
package com.project.booking.dto.shore;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityVisitUsageManagementResponse(
        Long id,
        Long bookingPassengerId,
        String passengerName,
        String bookingCode,

        UUID visitTourId,
        String activityName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer maxPassengers,
        BigDecimal price,
        String visitTourStatus,

        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal discountAmount,
        BigDecimal finalAmount,
        LocalDateTime usedAt) {
}
