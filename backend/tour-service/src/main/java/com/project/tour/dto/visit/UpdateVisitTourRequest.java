// src/main/java/com/project/tour/dto/visit/UpdateVisitTourRequest.java

package com.project.tour.dto.visit;

import com.project.tour.model.enums.visit.VisitTourStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateVisitTourRequest(

        UUID scheduleStopId,

        String name,

        String description,

        LocalDateTime startTime,

        LocalDateTime endTime,

        Integer maxPassengers,

        BigDecimal price,

        VisitTourStatus status) {
}