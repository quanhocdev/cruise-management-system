package com.project.tour.dto.tour.operation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AssignmentActivityCruiseResponse(
        UUID id,
        UUID tourId,
        UUID cruiseAreaId,

        UUID activityCruiseTourId,
        UUID activityCruiseId,

        String activityName,
        String activityDescription,

        LocalDateTime startTime,
        LocalDateTime endTime,

        Integer maxPassengers,
        BigDecimal price,

        String imageUrl,
        String status,

        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}