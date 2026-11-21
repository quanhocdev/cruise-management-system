
package com.project.booking.dto.passenger.benefit;

import java.util.List;

public record PassengerBenefitDTO(
        Long bookingId,
        List<PassengerBenefitConsumptionDTO> consumptions) {
}
