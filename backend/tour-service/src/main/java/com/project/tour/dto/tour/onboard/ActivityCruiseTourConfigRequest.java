package com.project.tour.dto.tour.onboard;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityCruiseTourConfigRequest(

        @NotNull(message = "Activity cruise is required") UUID activityCruiseId,

        @NotNull(message = "Start time is required") LocalDateTime startTime,

        @NotNull(message = "End time is required") LocalDateTime endTime,

        @NotNull(message = "Max passengers is required") @Positive(message = "Max passengers must be greater than 0") Integer maxPassengers,

        @NotNull(message = "Price is required") @DecimalMin(value = "0.0", inclusive = true, message = "Price must be greater than or equal to 0") BigDecimal price

) {
}