package com.project.tour.mapper.convenience;

import com.project.tour.dto.convenience.service.convenience.ServiceTourResponse;
import com.project.tour.model.convenience.service.Service;
import com.project.tour.model.convenience.service.ServiceTour;

import org.springframework.stereotype.Component;

@Component
public class ServiceTourMapper {

    public ServiceTourResponse toResponse(
            ServiceTour entity) {

        if (entity == null) {
            return null;
        }

        Service service = entity.getService();

        return new ServiceTourResponse(
                entity.getId(),

                // Tour info
                entity.getTourId(),
                null, // tourCode
                null, // tourName

                // Service info
                service != null ? service.getId() : null,
                service != null ? service.getName() : null,
                service != null ? service.getDescription() : null,
                service != null ? service.getPrice() : null,
                service != null ? service.getImageUrl() : null,

                // Cruise Area info
                entity.getCruiseAreaId(),
                null, // cruiseAreaName

                // Cruise Deck info
                null, // cruiseDeckId
                null, // deckNumber

                // Configuration
                entity.getMaxPassengers(),
                entity.getDurationMinutes(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}