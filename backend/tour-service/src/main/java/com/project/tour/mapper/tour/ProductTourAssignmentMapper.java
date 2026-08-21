package com.project.tour.mapper.tour;

import com.project.tour.dto.tour.convenience.product.ProductTourResponse;
import com.project.tour.dto.tour.operation.ProductTourAssignmentResponse;
import com.project.tour.model.ProductTour;

import org.springframework.stereotype.Component;

@Component
public class ProductTourAssignmentMapper {

    // =====================================================
    // OPERATION RESPONSE
    // =====================================================

    public ProductTourAssignmentResponse toResponse(ProductTour entity) {

        if (entity == null) {
            return null;
        }

        var tour = entity.getTour();
        var product = entity.getProduct();
        var cruiseArea = entity.getCruiseArea();
        var cruiseDeck = cruiseArea != null
                ? cruiseArea.getCruiseDeck()
                : null;

        return new ProductTourAssignmentResponse(
                entity.getId(),

                // Tour
                tour != null ? tour.getId() : null,
                tour != null ? tour.getCode() : null,
                tour != null ? tour.getName() : null,

                // Product
                product != null ? product.getId() : null,
                product != null ? product.getName() : null,

                // Cruise Area
                cruiseArea != null ? cruiseArea.getId() : null,
                cruiseArea != null ? cruiseArea.getName() : null,

                // Cruise Deck
                cruiseDeck != null ? cruiseDeck.getId() : null,
                cruiseDeck != null ? cruiseDeck.getDeckNumber() : null,

                entity.getQuantity(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    // =====================================================
    // CONVENIENCE / PRODUCT TOUR RESPONSE
    // =====================================================

    public ProductTourResponse toProductTourResponse(ProductTour entity) {

        if (entity == null) {
            return null;
        }

        var tour = entity.getTour();
        var product = entity.getProduct();
        var cruiseArea = entity.getCruiseArea();
        var cruiseDeck = cruiseArea != null
                ? cruiseArea.getCruiseDeck()
                : null;

        return new ProductTourResponse(

                entity.getId(),

                // =========================
                // TOUR
                // =========================

                tour != null ? tour.getId() : null,
                tour != null ? tour.getCode() : null,
                tour != null ? tour.getName() : null,

                // =========================
                // PRODUCT
                // =========================

                product != null ? product.getId() : null,
                product != null ? product.getName() : null,
                product != null ? product.getDescription() : null,
                product != null ? product.getImageUrl() : null,

                // =========================
                // CRUISE AREA
                // =========================

                cruiseArea != null ? cruiseArea.getId() : null,
                cruiseArea != null ? cruiseArea.getName() : null,

                // =========================
                // CRUISE DECK
                // =========================

                cruiseDeck != null ? cruiseDeck.getId() : null,
                cruiseDeck != null ? cruiseDeck.getDeckNumber() : null,

                // =========================
                // CONFIG
                // =========================

                entity.getQuantity(),
                entity.getStatus(),

                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}