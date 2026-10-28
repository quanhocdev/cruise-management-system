
package com.project.tour.mapper.scheduler;

import com.project.tour.dto.scheduler.TourRouteResponse;
import com.project.tour.model.scheduler.TourRoute;

public final class TourRouteMapper {

    private TourRouteMapper() {
    }

    public static TourRouteResponse toResponse(TourRoute route) {
        if (route == null) {
            return null;
        }

        return new TourRouteResponse(
                route.getId(),
                route.getTour().getId(),
                route.getDeparturePort().getId(),
                route.getDeparturePort().getName(),
                route.getDestinationPort().getId(),
                route.getDestinationPort().getName(),
                route.getRouteGeoJson(),
                route.getDistanceKm(),
                route.getDistanceFromDepartureKm(),
                route.getDistanceToDestinationKm(),
                route.getCreatedAt(),
                route.getUpdatedAt());
    }
}
