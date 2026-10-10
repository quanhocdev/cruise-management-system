package com.project.booking.dto.onboard;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityCruiseUsageManagementInfo(
        UUID activityCruiseTourId,
        UUID tourId,
        String activityName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer maxPassengers,
        BigDecimal price,
        String activityCruiseTourStatus) {
}