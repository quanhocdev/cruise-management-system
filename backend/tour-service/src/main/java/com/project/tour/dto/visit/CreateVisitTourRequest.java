// src/main/java/com/project/tour/dto/visit/CreateVisitTourRequest.java

package com.project.tour.dto.visit;

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