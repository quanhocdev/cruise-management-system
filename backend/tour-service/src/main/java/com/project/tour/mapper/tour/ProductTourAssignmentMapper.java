package com.project.tour.mapper.tour;

import com.project.tour.dto.tour.operation.ProductTourAssignmentRequest;
import com.project.tour.dto.tour.operation.ProductTourAssignmentResponse;
import com.project.tour.model.AssignmentProduct;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.CruiseDeck;
import com.project.tour.model.Tour;
import org.springframework.stereotype.Component;

@Component
public class ProductTourAssignmentMapper {

    /**
     * Map Request -> Entity để lưu Database.
     */
    public AssignmentProduct toEntity(ProductTourAssignmentRequest request) {
        if (request == null) {
            return null;
        }

        AssignmentProduct entity = new AssignmentProduct();
        entity.setTourId(request.tourId());
        entity.setCruiseAreaId(request.cruiseAreaId());
        return entity;
    }

    /**
     * Map Entity -> Response kèm theo dữ liệu đầy đủ từ Tour & CruiseArea.
     */
    public ProductTourAssignmentResponse toResponse(
            AssignmentProduct entity,
            Tour tour,
            CruiseArea cruiseArea) {

        if (entity == null) {
            return null;
        }

        // An toàn tránh NullPointerException khi lấy thông tin Deck
        CruiseDeck cruiseDeck = (cruiseArea != null) ? cruiseArea.getCruiseDeck() : null;

        return new ProductTourAssignmentResponse(
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