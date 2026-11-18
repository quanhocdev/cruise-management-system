
package com.project.booking.dto.passenger.usage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PassengerActivityVisitUsageDTO(
        Long id,
        Long bookingPassengerId,
        UUID visitTourId,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal discountAmount,
        BigDecimal finalAmount,
        LocalDateTime usedAt,
        LocalDateTime endedAt) {
}
