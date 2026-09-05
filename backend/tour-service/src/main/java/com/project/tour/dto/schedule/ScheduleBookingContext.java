package com.project.tour.dto.schedule;

import java.time.LocalDate;
import java.util.UUID;

public record ScheduleBookingContext(
    UUID voyageId, Integer capacity, LocalDate startDate, String status
) {}
