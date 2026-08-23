package com.project.tour.dto.tour.convenience.service;

import com.project.tour.model.enums.convenience.ServiceTourStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ServiceTourResponse(

        UUID id,

        // Tour
        UUID tourId,
        String tourCode,
        String tourName,

        // Service
        UUID serviceId,
        String serviceName,
        String serviceDescription,
        BigDecimal servicePrice,
        String serviceImageUrl,

        // Cruise Area
        UUID cruiseAreaId,
        String cruiseAreaName,

        // Cruise Deck
        UUID cruiseDeckId,
        Integer deckNumber,

        // Configuration for this Tour
        Integer maxPassengers,
        Integer durationMinutes,

        ServiceTourStatus status,

        Instant createdAt,
        Instant updatedAt

) {
}