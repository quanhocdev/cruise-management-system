package com.project.tour.mapper.tour;

import com.project.tour.dto.tour.operation.ServiceTourAssignmentRequest;
import com.project.tour.dto.tour.operation.ServiceTourAssignmentResponse;
import com.project.tour.model.AssignmentService;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.CruiseDeck;
import com.project.tour.model.Tour;
import org.springframework.stereotype.Component;

@Component
public class ServiceTourAssignmentMapper {

        /**
         * Map Request -> Entity để lưu Database bên tour-service.
         */
        public AssignmentService toEntity(ServiceTourAssignmentRequest request) {
                if (request == null) {
                        return null;
                }

                AssignmentService entity = new AssignmentService();
                entity.setTourId(request.tourId());
                entity.setCruiseAreaId(request.cruiseAreaId());
                return entity;
        }

        /**
         * Map Entity -> Response kèm theo thông tin hiển thị từ Tour & CruiseArea.
         */
        public ServiceTourAssignmentResponse toResponse(
                        AssignmentService entity,
                        Tour tour,
                        CruiseArea cruiseArea) {

                if (entity == null) {
                        return null;
                }

                CruiseDeck cruiseDeck = (cruiseArea != null) ? cruiseArea.getCruiseDeck() : null;

                return new ServiceTourAssignmentResponse(
                                // Assignment Info
                                entity.getId(),

                                // Tour Info
                                entity.getTourId(),
                                tour != null ? tour.getCode() : null,
                                tour != null ? tour.getName() : null,

                                // Cruise Area Info
                                entity.getCruiseAreaId(),
                                cruiseArea != null ? cruiseArea.getName() : null,

                                // Cruise Deck Info
                                cruiseDeck != null ? cruiseDeck.getId() : null,
                                cruiseDeck != null ? cruiseDeck.getDeckNumber() : null,

                                // Timestamps
                                entity.getCreatedAt(),
                                entity.getUpdatedAt());
        }
}