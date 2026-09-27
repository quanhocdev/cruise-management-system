package com.project.tour.dto.operation.packages.benefit;

import com.project.tour.model.enums.tour.BenefitType;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record PackageBenefitRequest(
                @NotNull BenefitType type,
                @NotNull UUID referenceId,
                Integer quantity,
                BigDecimal discountPercent) {
}