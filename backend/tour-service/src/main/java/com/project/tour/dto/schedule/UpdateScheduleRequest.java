package com.project.tour.dto.schedule;

import com.project.tour.model.enums.ScheduleStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateScheduleRequest(
    @NotNull(message = "Tour package is required") UUID tourPackageId,
    @NotNull(message = "Cruise is required") UUID cruiseId,
    @NotNull(message = "Start date is required") LocalDate startDate,
    @NotNull(message = "End date is required") LocalDate endDate,
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1") Integer capacity,
    @NotNull(message = "Schedule status is required") ScheduleStatus status
) {}
