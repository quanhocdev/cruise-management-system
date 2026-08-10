package com.project.tour.dto.policy;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateBookingPolicyRequest(
    @NotNull @Min(value = 0, message = "Days before departure must be non-negative") Integer daysBeforeDeparture,
    @NotNull @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal discountPercent
) {}
