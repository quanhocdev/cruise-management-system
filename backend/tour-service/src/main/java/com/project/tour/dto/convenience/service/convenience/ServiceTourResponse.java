package com.project.tour.dto.convenience.service.convenience;

import com.project.tour.model.convenience.enums.ServiceTourStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ServiceTourResponse(
        UUID id,

        UUID tourId,
        String tourCode,
        String tourName,

        UUID serviceId,
        String serviceName,
        String serviceDescription,
        BigDecimal servicePrice,
        String serviceImageUrl,

        UUID cruiseAreaId,
        String cruiseAreaName,

        UUID cruiseDeckId,
        Integer deckNumber,

        Integer maxPassengers,
        Integer durationMinutes,

        ServiceTourStatus status,

        Instant createdAt,
        Instant updatedAt
) {
}