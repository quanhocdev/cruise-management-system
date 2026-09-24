package com.project.tour.mapper.tour;

import com.project.tour.dto.tour.operation.ServiceTourAssignmentRequest;
import com.project.tour.dto.tour.operation.ServiceTourAssignmentResponse;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.CruiseDeck;
import com.project.tour.model.Tour;
import com.project.tour.model.convenience.service.ServiceTour;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class ServiceTourAssignmentMapper {

        /**
         * Map Request -> Entity.
         */
        public ServiceTour toEntity(
                        ServiceTourAssignmentRequest request) {

                if (request == null) {
                        return null;
                }

                ServiceTour entity = new ServiceTour();

                entity.setTourId(request.tourId());
                entity.setCruiseAreaId(request.cruiseAreaId());

                return entity;
        }

        /**
         * Map Entity -> Response kèm thông tin Tour & CruiseArea.
         */
        public ServiceTourAssignmentResponse toResponse(
                        ServiceTour entity,
                        Tour tour,
                        CruiseArea cruiseArea) {

                if (entity == null) {
                        return null;
                }

                CruiseDeck cruiseDeck = cruiseArea != null
                                ? cruiseArea.getCruiseDeck()
                                : null;

                return new ServiceTourAssignmentResponse(
                                // Assignment Info
                                entity.getId(),

                                // Tour Info
                                entity.getTourId(),
                                tour != null
                                                ? tour.getCode()
                                                : null,
                                tour != null
                                                ? tour.getName()
                                                : null,

                                // Cruise Area Info
                                entity.getCruiseAreaId(),
                                cruiseArea != null
                                                ? cruiseArea.getName()
                                                : null,

                                // Cruise Deck Info
                                cruiseDeck != null
                                                ? cruiseDeck.getId()
                                                : null,
                                cruiseDeck != null
                                                ? cruiseDeck.getDeckNumber()
                                                : null,

                                // Timestamps
                                toLocalDateTime(entity.getCreatedAt()),
                                toLocalDateTime(entity.getUpdatedAt()));
        }

        private LocalDateTime toLocalDateTime(
                        Instant instant) {

                if (instant == null) {
                        return null;
                }

                return LocalDateTime.ofInstant(
                                instant,
                                ZoneId.systemDefault());
        }
}