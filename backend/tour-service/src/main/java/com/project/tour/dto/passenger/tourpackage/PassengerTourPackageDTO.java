package com.project.tour.dto.passenger.tourpackage;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PassengerTourPackageDTO(
        UUID id,
        UUID tourId,
        UUID roomTypeId,
        String roomTypeName,
        String name,
        String description,
        BigDecimal price,
        List<PassengerPackageBenefitDTO> benefits) {
}