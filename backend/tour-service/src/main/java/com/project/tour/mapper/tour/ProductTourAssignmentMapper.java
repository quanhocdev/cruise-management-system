package com.project.tour.mapper.tour;

import com.project.tour.dto.tour.operation.ProductTourAssignmentRequest;
import com.project.tour.dto.tour.operation.ProductTourAssignmentResponse;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.CruiseDeck;
import com.project.tour.model.Tour;
import com.project.tour.model.convenience.product.ProductTour;

import org.springframework.stereotype.Component;

@Component
public class ProductTourAssignmentMapper {

    /**
     * Map Request -> Entity.
     */
    public ProductTour toEntity(
            ProductTourAssignmentRequest request) {

        if (request == null) {
            return null;
        }

        ProductTour entity = new ProductTour();

        entity.setTourId(request.tourId());
        entity.setCruiseAreaId(request.cruiseAreaId());

        return entity;
    }

    /**
     * Map Entity -> Response kèm thông tin Tour & CruiseArea.
     */
    public ProductTourAssignmentResponse toResponse(
            ProductTour entity,
            Tour tour,
            CruiseArea cruiseArea) {

        if (entity == null) {
            return null;
        }

        CruiseDeck cruiseDeck = cruiseArea != null
                ? cruiseArea.getCruiseDeck()
                : null;

        return new ProductTourAssignmentResponse(
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
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}