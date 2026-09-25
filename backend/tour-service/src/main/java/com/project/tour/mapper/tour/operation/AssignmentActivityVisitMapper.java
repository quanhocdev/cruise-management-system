package com.project.tour.mapper.tour.operation;

import com.project.tour.dto.tour.operation.AssignmentActivityVisitResponse;
import com.project.tour.model.activityvisit.VisitTour;

public final class AssignmentActivityVisitMapper {

    private AssignmentActivityVisitMapper() {
    }

    public static AssignmentActivityVisitResponse toResponse(
            VisitTour visitTour) {

        return new AssignmentActivityVisitResponse(
                visitTour.getId(),
                visitTour.getTourId(),
                visitTour.getScheduleStopId(),
                visitTour.getId(),
                visitTour.getName(),
                visitTour.getDescription(),
                visitTour.getStartTime(),
                visitTour.getEndTime(),
                visitTour.getMaxPassengers(),
                visitTour.getPrice(),
                visitTour.getStatus() != null
                        ? visitTour.getStatus().name()
                        : null,
                visitTour.getCreatedAt(),
                visitTour.getUpdatedAt());
    }
}
