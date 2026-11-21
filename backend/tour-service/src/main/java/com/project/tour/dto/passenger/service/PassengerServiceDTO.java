package com.project.tour.dto.passenger.service;

import java.math.BigDecimal;
import java.util.UUID;

public record PassengerServiceDTO(
        UUID id,
        UUID serviceTourId,
        UUID cruiseAreaId,
        String name,
        String description,
        BigDecimal price,
        Integer durationMinutes,
        Integer maxPassengers,
        String imageUrl) {
}