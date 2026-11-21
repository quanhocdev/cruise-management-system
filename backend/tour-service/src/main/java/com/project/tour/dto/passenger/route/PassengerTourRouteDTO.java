package com.project.tour.dto.passenger.route;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record PassengerTourRouteDTO(
        UUID tourId,
        String tourCode,
        String tourName,
        PassengerPortDTO departurePort,
        PassengerPortDTO destinationPort,
        Map<String, Object> routeGeoJson,
        Double distanceKm,
        Double distanceFromDepartureKm,
        Double distanceToDestinationKm,
        List<PassengerRouteLegDTO> legs) {
}