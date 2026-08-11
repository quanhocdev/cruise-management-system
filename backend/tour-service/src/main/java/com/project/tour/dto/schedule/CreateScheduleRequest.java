package com.project.tour.dto.schedule;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;

public record CreateScheduleRequest(
    @NotNull(message = "Tour package is required") UUID tourPackageId,
    @NotNull(message = "Cruise is required") UUID cruiseId,
    @NotBlank(message = "Schedule code is required")
    @Size(max = 50, message = "Schedule code must not exceed 50 characters") String code,
    @NotNull(message = "Start date is required") LocalDate startDate,
    @NotNull(message = "End date is required") LocalDate endDate,
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1") Integer capacity
) {}
