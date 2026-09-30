package com.project.tour.mapper.operation.configured;

import com.project.tour.dto.operation.configured.AssignmentActivityVisitResponse;
import com.project.tour.model.shore.VisitTour;

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
