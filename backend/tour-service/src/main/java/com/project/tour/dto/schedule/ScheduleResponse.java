package com.project.tour.dto.schedule;

import com.project.tour.model.enums.ScheduleStatus;
import java.time.LocalDate;
import java.util.UUID;

public record ScheduleResponse(
    UUID id,
    UUID tourPackageId,
    String tourPackageName,
    UUID cruiseId,
    String cruiseName,
    String code,
    LocalDate startDate,
    LocalDate endDate,
    Integer capacity,
    ScheduleStatus status
) {}
