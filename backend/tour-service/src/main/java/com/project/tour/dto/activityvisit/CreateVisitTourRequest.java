package com.project.tour.dto.activityvisit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateVisitTourRequest(

        String name,

        String description,

        LocalDateTime startTime,

        LocalDateTime endTime,

        Integer maxPassengers,

        BigDecimal price) {
}
