package com.project.tour.dto.passenger.route;

import java.util.Map;
import java.util.UUID;

public record PassengerRouteLegDTO(
        UUID id,
        Integer legOrder,
        PassengerPortDTO fromPort,
        PassengerPortDTO toPort,
        Map<String, Object> routeGeoJson,
        Double distanceKm) {
}