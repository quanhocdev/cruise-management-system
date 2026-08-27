package com.project.common.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityCruiseTourConfiguredEvent(

        UUID activityCruiseTourId,

        UUID tourId,

        UUID cruiseAreaId,

        UUID activityCruiseId,

        String name,

        String description,

        LocalDateTime startTime,

        LocalDateTime endTime,

        Integer maxPassengers,

        BigDecimal price,

        String imageUrl,

        String status

) {
}