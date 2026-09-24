package com.project.tour.dto.convenience.service.convenience;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ServiceTourConfigRequest(

        @NotNull(message = "Service ID is required") UUID serviceId,

        @NotNull(message = "Max passengers is required") @Positive(message = "Max passengers must be greater than 0") Integer maxPassengers,

        @Positive(message = "Duration must be greater than 0") Integer durationMinutes) {
}