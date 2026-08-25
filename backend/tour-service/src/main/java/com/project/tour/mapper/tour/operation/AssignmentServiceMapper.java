package com.project.tour.mapper.tour.operation;

import com.project.tour.dto.tour.operation.AssignmentServiceResponse;
import com.project.tour.model.AssignmentService;

public class AssignmentServiceMapper {

    private AssignmentServiceMapper() {
    }

    public static AssignmentServiceResponse toResponse(
            AssignmentService assignment) {

        return new AssignmentServiceResponse(
                assignment.getId(),
                assignment.getTourId(),
                assignment.getCruiseAreaId(),
                assignment.getServiceTourId(),
                assignment.getServiceId(),
                assignment.getServiceName(),
                assignment.getServiceDescription(),
                assignment.getPrice(),
                assignment.getMaxPassengers(),
                assignment.getDurationMinutes(),
                assignment.getImageUrl(),
                assignment.getStatus(),
                assignment.getCreatedAt(),
                assignment.getUpdatedAt());
    }
}