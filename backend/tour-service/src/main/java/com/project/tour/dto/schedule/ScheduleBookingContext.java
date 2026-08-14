package com.project.tour.dto.schedule;

import com.project.tour.model.enums.ScheduleStatus;
import java.time.LocalDate;
import java.util.UUID;

public record ScheduleBookingContext(
    UUID voyageId, Integer capacity, LocalDate startDate, ScheduleStatus status
) {}
