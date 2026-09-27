package com.project.tour.dto.shore;

import com.project.tour.model.shore.enums.VisitTourStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record VisitTourResponse(

        UUID id,

        UUID tourId,

        UUID scheduleStopId,

        String name,

        String description,

        LocalDateTime startTime,

        LocalDateTime endTime,

        Integer maxPassengers,

        BigDecimal price,

        VisitTourStatus status,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}
