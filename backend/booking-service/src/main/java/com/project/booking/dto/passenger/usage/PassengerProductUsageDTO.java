
package com.project.booking.dto.passenger.usage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PassengerProductUsageDTO(
        Long id,
        Long bookingPassengerId,
        UUID productTourId,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal discountAmount,
        BigDecimal finalAmount,
        LocalDateTime usedAt) {
}
