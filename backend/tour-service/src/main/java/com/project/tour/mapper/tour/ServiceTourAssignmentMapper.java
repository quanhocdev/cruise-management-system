package com.project.tour.mapper.tour;

import com.project.tour.dto.tour.operation.ServiceTourAssignmentResponse;
import com.project.tour.model.ServiceTour;
import org.springframework.stereotype.Component;

@Component
public class ServiceTourAssignmentMapper {

        public ServiceTourAssignmentResponse toResponse(ServiceTour entity) {

                return new ServiceTourAssignmentResponse(

                                // =====================================================
                                // ASSIGNMENT
                                // =====================================================

                                entity.getId(),

                                // =====================================================
                                // TOUR
                                // =====================================================

                                entity.getTour() != null
                                                ? entity.getTour().getId()
                                                : null,

                                entity.getTour() != null
                                                ? entity.getTour().getCode()
                                                : null,

                                entity.getTour() != null
                                                ? entity.getTour().getName()
                                                : null,

                                // =====================================================
                                // CRUISE AREA
                                // =====================================================

                                entity.getCruiseArea() != null
                                                ? entity.getCruiseArea().getId()
                                                : null,

                                entity.getCruiseArea() != null
                                                ? entity.getCruiseArea().getName()
                                                : null,

                                // =====================================================
                                // CRUISE DECK
                                // =====================================================

                                entity.getCruiseArea() != null
                                                && entity.getCruiseArea().getCruiseDeck() != null
                                                                ? entity.getCruiseArea().getCruiseDeck().getId()
                                                                : null,

                                entity.getCruiseArea() != null
                                                && entity.getCruiseArea().getCruiseDeck() != null
                                                                ? entity.getCruiseArea().getCruiseDeck().getDeckNumber()
                                                                : null,

                                // =====================================================
                                // SERVICE
                                // =====================================================

                                entity.getService() != null
                                                ? entity.getService().getId()
                                                : null,

                                entity.getService() != null
                                                ? entity.getService().getName()
                                                : null,

                                entity.getService() != null
                                                ? entity.getService().getDescription()
                                                : null,

                                // =====================================================
                                // CONFIGURATION
                                // =====================================================

                                entity.getMaxPassengers(),

                                entity.getDurationMinutes(),

                                // =====================================================
                                // STATUS
                                // =====================================================

                                entity.getStatus(),

                                // =====================================================
                                // TIMESTAMP
                                // =====================================================

                                entity.getCreatedAt(),

                                entity.getUpdatedAt());
        }
}