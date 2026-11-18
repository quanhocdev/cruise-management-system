package com.project.tour.dto.passenger.activity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PassengerVisitTourDTO(
        UUID id,
        UUID scheduleStopId,
        String name,
        String description,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer maxPassengers,
        BigDecimal price) {
}