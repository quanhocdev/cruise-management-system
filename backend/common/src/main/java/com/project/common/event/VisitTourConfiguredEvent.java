package com.project.common.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record VisitTourConfiguredEvent(

        UUID visitTourId,

        UUID tourId,

        UUID scheduleStopId,

        String name,

        String description,

        LocalDateTime startTime,

        LocalDateTime endTime,

        Integer maxPassengers,

        BigDecimal price,

        String status

) {
}