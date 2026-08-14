package com.project.booking.client;

import java.time.LocalDate;
import java.util.UUID;

public record TourScheduleContext(
    UUID voyageId, Integer capacity, LocalDate startDate, String status
) {}
