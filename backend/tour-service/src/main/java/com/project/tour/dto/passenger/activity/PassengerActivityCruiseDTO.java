package com.project.tour.dto.passenger.activity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PassengerActivityCruiseDTO(
        UUID id,
        UUID activityCruiseTourId,
        UUID cruiseAreaId,
        String name,
        String description,
        String imageUrl,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer maxPassengers,
        BigDecimal price) {
}