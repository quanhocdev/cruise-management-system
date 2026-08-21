package com.project.tour.dto.visit;

import com.project.tour.model.enums.visit.VisitTourStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateVisitTourRequest(

        String name,

        String description,

        LocalDateTime startTime,

        LocalDateTime endTime,

        Integer maxPassengers,

        BigDecimal price,

        VisitTourStatus status) {
}