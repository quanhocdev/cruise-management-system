package com.project.tour.dto.activitycruise;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.project.tour.model.activitycruise.enums.ActivityCruiseTourStatus;

public record ActivityCruiseTourResponse(

        UUID id,

        UUID tourId,
        String tourCode,
        String tourName,

        // Activity info
        UUID activityCruiseId,
        String activityCruiseName,
        String activityCruiseDescription,
        String activityCruiseImageUrl,

        // Cruise Area info
        UUID cruiseAreaId,
        String cruiseAreaName,

        // Configuration & Timings
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer maxPassengers,
        BigDecimal price,
        ActivityCruiseTourStatus status,

        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

}
