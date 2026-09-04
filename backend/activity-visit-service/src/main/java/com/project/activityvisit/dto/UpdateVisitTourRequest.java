package com.project.activityvisit.dto;

import com.project.activityvisit.model.enums.VisitTourStatus;

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