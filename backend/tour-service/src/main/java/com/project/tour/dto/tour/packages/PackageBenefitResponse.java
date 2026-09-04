package com.project.tour.dto.tour.packages;

import com.project.tour.model.enums.tour.BenefitType;
import java.math.BigDecimal;
import java.util.UUID;

public record PackageBenefitResponse(
        UUID id,
        UUID tourPackageId,
        BenefitType type,
        UUID referenceId,
        Integer freeQuantity,
        BigDecimal discountPercent) {
}