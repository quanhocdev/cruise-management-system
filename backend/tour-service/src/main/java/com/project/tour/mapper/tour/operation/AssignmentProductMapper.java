package com.project.tour.mapper.tour.operation;

import com.project.tour.dto.tour.operation.AssignmentProductResponse;
import com.project.tour.model.convenience.product.ProductTour;

public final class AssignmentProductMapper {

    private AssignmentProductMapper() {
    }

    public static AssignmentProductResponse toResponse(
            ProductTour productTour) {

        if (productTour == null) {
            return null;
        }

        return new AssignmentProductResponse(
                productTour.getId(),
                productTour.getTourId(),
                productTour.getCruiseAreaId(),
                productTour.getId(),
                productTour.getProduct() != null
                        ? productTour.getProduct().getId()
                        : null,
                productTour.getProduct() != null
                        ? productTour.getProduct().getName()
                        : null,
                productTour.getProduct() != null
                        ? productTour.getProduct().getDescription()
                        : null,
                productTour.getProduct() != null
                        ? productTour.getProduct().getPrice()
                        : null,
                productTour.getQuantity(),
                productTour.getProduct() != null
                        ? productTour.getProduct().getImageUrl()
                        : null,
                productTour.getStatus() != null
                        ? productTour.getStatus().name()
                        : null);
    }
}