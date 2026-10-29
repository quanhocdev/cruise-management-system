
package com.project.tour.dto.scheduler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record TourRouteResponse(

        UUID id,

        UUID tourId,

        UUID departurePortId,

        String departurePortName,

        UUID destinationPortId,

        String destinationPortName,

        Map<String, Object> routeGeoJson,

        Double distanceKm,

        Double distanceFromDepartureKm,

        Double distanceToDestinationKm,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}
