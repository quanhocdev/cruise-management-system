// src/main/java/com/project/tour/dto/visit/CreateVisitTourRequest.java

package com.project.tour.dto.visit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateVisitTourRequest(

        UUID scheduleStopId,

        String name,

        String description,

        LocalDateTime startTime,

        LocalDateTime endTime,

        Integer maxPassengers,

        BigDecimal price) {
}