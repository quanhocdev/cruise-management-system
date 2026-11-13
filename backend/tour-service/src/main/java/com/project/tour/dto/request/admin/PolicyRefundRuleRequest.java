
package com.project.tour.dto.request.admin;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PolicyRefundRuleRequest(

        @NotNull(message = "Minimum days before departure is required") @Min(value = 0, message = "Minimum days cannot be negative") Integer minDaysBeforeDeparture,

        @Min(value = 0, message = "Maximum days cannot be negative") Integer maxDaysBeforeDeparture,

        @NotNull(message = "Refund percentage is required") @DecimalMin(value = "0.00", message = "Refund percentage cannot be less than 0") @DecimalMax(value = "100.00", message = "Refund percentage cannot exceed 100") BigDecimal refundPercentage,

        @Size(max = 500, message = "Description must not exceed 500 characters") String description

) {
}
