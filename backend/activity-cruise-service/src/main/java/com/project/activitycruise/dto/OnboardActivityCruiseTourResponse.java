package com.project.activitycruise.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.project.activitycruise.model.enums.ActivityCruiseTourStatus;

public record OnboardActivityCruiseTourResponse(
                UUID id,

                // Tour info (Thuộc Tour Service - populate qua Feign sau)
                UUID tourId,
                String tourCode,
                String tourName,

                // Activity info (Thuộc Activity Cruise Service)
                UUID activityCruiseId,
                String activityCruiseName,
                String activityCruiseDescription,
                String activityCruiseImageUrl,

                // Cruise Area info (Thuộc Tour Service - populate qua Feign sau)
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