package com.project.tour.dto.policy;

import com.project.tour.model.enums.PolicyStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record UpdateCancelPolicyRequest(
    @NotNull @Min(value = 0, message = "Days before must be non-negative") Integer daysBefore,
    @NotNull @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal refundPercent,
    @NotNull PolicyStatus status
) {}
