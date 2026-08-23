package com.project.convenience.mapper;

import com.project.convenience.dto.service.convenience.ServiceTourResponse;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.Service;
import com.project.tour.model.ServiceTour;

import org.springframework.stereotype.Component;

@Component
public class ServiceTourMapper {

        public ServiceTourResponse toResponse(ServiceTour entity) {

                if (entity == null) {
                        return null;
                }

                var tour = entity.getTour();
                Service service = entity.getService();
                CruiseArea cruiseArea = entity.getCruiseArea();

                var cruiseDeck = cruiseArea != null
                                ? cruiseArea.getCruiseDeck()
                                : null;

                return new ServiceTourResponse(

                                entity.getId(),

                                // =====================================================
                                // TOUR
                                // =====================================================

                                tour != null
                                                ? tour.getId()
                                                : null,

                                tour != null
                                                ? tour.getCode()
                                                : null,

                                tour != null
                                                ? tour.getName()
                                                : null,

                                // =====================================================
                                // SERVICE
                                // =====================================================

                                service != null
                                                ? service.getId()
                                                : null,

                                service != null
                                                ? service.getName()
                                                : null,

                                service != null
                                                ? service.getDescription()
                                                : null,

                                service != null
                                                ? service.getPrice()
                                                : null,

                                service != null
                                                ? service.getImageUrl()
                                                : null,

                                // =====================================================
                                // CRUISE AREA
                                // =====================================================

                                cruiseArea != null
                                                ? cruiseArea.getId()
                                                : null,

                                cruiseArea != null
                                                ? cruiseArea.getName()
                                                : null,

                                // =====================================================
                                // CRUISE DECK
                                // =====================================================

                                cruiseDeck != null
                                                ? cruiseDeck.getId()
                                                : null,

                                cruiseDeck != null
                                                ? cruiseDeck.getDeckNumber()
                                                : null,

                                // =====================================================
                                // TOUR CONFIG
                                // =====================================================

                                entity.getMaxPassengers(),

                                entity.getDurationMinutes(),

                                entity.getStatus(),

                                entity.getCreatedAt(),

                                entity.getUpdatedAt());
        }
}