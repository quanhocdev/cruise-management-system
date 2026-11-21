package com.project.tour.dto.route;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record TourRouteLegResponse(
        UUID legId,
        Integer legOrder,
        UUID fromPortId,
        String fromPortName,
        UUID toPortId,
        String toPortName,
        Double distanceKm,
        Map<String, Object> routeGeoJson,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}