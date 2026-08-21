// src/main/java/com/project/tour/dto/visit/VisitTourResponse.java

package com.project.tour.dto.visit;

import com.project.tour.model.enums.visit.VisitTourStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record VisitTourResponse(

        UUID id,

        // TOUR
        UUID tourId,
        String tourCode,
        String tourName,

        // SCHEDULE
        UUID scheduleId,
        Integer dayNumber,

        // SCHEDULE STOP
        UUID scheduleStopId,
        Integer stopOrder,

        // PORT
        UUID portId,
        String portName,

        // THỜI GIAN TÀU
        LocalDateTime arriveAt,
        LocalDateTime leaveAt,

        // VISIT TOUR
        String name,
        String description,

        LocalDateTime startTime,
        LocalDateTime endTime,

        Integer maxPassengers,

        BigDecimal price,

        VisitTourStatus status,

        // AUDIT
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}