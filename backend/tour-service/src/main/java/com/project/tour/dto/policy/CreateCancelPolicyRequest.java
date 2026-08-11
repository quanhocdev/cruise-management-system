package com.project.tour.dto.policy;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateCancelPolicyRequest(
    @NotNull @Min(value = 0, message = "Days before must be non-negative") Integer daysBefore,
    @NotNull @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal refundPercent
) {}
