package com.project.tour.dto.tour.operation;

import com.project.tour.model.enums.onboard.ActivityCruiseTourStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityCruiseTourAssignmentResponse(

        UUID id,

        UUID tourId,
        String tourCode,
        String tourName,

        UUID activityCruiseId,
        String activityCruiseName,

        UUID cruiseAreaId,
        String cruiseAreaName,

        UUID cruiseDeckId,
        Integer deckNumber,

        LocalDateTime startTime,
        LocalDateTime endTime,

        Integer maxPassengers,

        BigDecimal price,

        ActivityCruiseTourStatus status,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}