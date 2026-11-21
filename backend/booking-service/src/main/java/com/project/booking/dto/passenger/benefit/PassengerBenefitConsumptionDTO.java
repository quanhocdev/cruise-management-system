
package com.project.booking.dto.passenger.benefit;

import java.time.LocalDateTime;
import java.util.UUID;

public record PassengerBenefitConsumptionDTO(
        Long id,
        Long bookingId,
        UUID packageBenefitId,
        Integer usedQuantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
