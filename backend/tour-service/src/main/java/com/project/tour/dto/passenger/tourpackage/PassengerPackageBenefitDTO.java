package com.project.tour.dto.passenger.tourpackage;

import com.project.tour.model.enums.tour.BenefitType;

import java.util.UUID;

public record PassengerPackageBenefitDTO(
        UUID id,
        BenefitType benefitType,
        UUID referenceId,
        String itemName,
        Integer quantity,
        Integer discountPercent) {
}