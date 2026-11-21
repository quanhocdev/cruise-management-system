package com.project.tour.dto.route;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record TourRouteOverviewResponse(
        UUID tourId,
        UUID departurePortId,
        String departurePortName,
        UUID destinationPortId,
        String destinationPortName,
        Double distanceKm,
        Double distanceFromDepartureKm,
        Double distanceToDestinationKm,
        Map<String, Object> routeGeoJson,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}