package com.project.tour.mapper.tour.operation;

import com.project.tour.dto.tour.operation.AssignmentActivityVisitResponse;
import com.project.tour.model.AssignmentActivityVisit;

public final class AssignmentActivityVisitMapper {

    private AssignmentActivityVisitMapper() {
    }

    public static AssignmentActivityVisitResponse toResponse(
            AssignmentActivityVisit assignment) {

        return new AssignmentActivityVisitResponse(
                assignment.getId(),
                assignment.getTourId(),
                assignment.getScheduleStopId(),
                assignment.getVisitTourId(),
                assignment.getVisitName(),
                assignment.getVisitDescription(),
                assignment.getStartTime(),
                assignment.getEndTime(),
                assignment.getMaxPassengers(),
                assignment.getPrice(),
                assignment.getStatus(),
                assignment.getCreatedAt(),
                assignment.getUpdatedAt());
    }
}