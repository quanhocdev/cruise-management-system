package com.project.tour.mapper.tour;

import com.project.tour.dto.tour.operation.ProductTourAssignmentResponse;
import com.project.tour.model.ProductTour;
import org.springframework.stereotype.Component;

@Component
public class ProductTourAssignmentMapper {

    public ProductTourAssignmentResponse toResponse(ProductTour entity) {
        if (entity == null) {
            return null;
        }

        var tour = entity.getTour();
        var product = entity.getProduct();
        var cruiseArea = entity.getCruiseArea();
        var cruiseDeck = (cruiseArea != null) ? cruiseArea.getCruiseDeck() : null;

        return new ProductTourAssignmentResponse(
                entity.getId(),

                // Tour info
                tour != null ? tour.getId() : null,
                tour != null ? tour.getCode() : null,
                tour != null ? tour.getName() : null,

                // Product info (Null khi mới được Operation phân công)
                product != null ? product.getId() : null,
                product != null ? product.getName() : null,

                // Cruise Area info
                cruiseArea != null ? cruiseArea.getId() : null,
                cruiseArea != null ? cruiseArea.getName() : null,

                // Cruise Deck info
                cruiseDeck != null ? cruiseDeck.getId() : null,
                cruiseDeck != null ? cruiseDeck.getDeckNumber() : null,

                entity.getQuantity(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}