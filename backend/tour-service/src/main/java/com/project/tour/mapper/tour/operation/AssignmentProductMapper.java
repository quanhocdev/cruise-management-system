package com.project.tour.mapper.tour.operation;

import com.project.tour.dto.tour.operation.AssignmentProductResponse;
import com.project.tour.model.AssignmentProduct;

public final class AssignmentProductMapper {

    private AssignmentProductMapper() {
    }

    public static AssignmentProductResponse toResponse(
            AssignmentProduct assignment) {

        return new AssignmentProductResponse(
                assignment.getId(),
                assignment.getTourId(),
                assignment.getCruiseAreaId(),
                assignment.getProductTourId(),
                assignment.getProductId(),
                assignment.getProductName(),
                assignment.getProductDescription(),
                assignment.getPrice(),
                assignment.getQuantity(),
                assignment.getImageUrl(),
                assignment.getStatus());
    }
}