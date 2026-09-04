package com.project.tour.mapper.tour.operation;

import com.project.tour.dto.tour.operation.AssignmentActivityCruiseResponse;
import com.project.tour.model.AssignmentActivityCruise;

public class AssignmentActivityCruiseMapper {

    private AssignmentActivityCruiseMapper() {
    }

    public static AssignmentActivityCruiseResponse toResponse(
            AssignmentActivityCruise entity) {

        return new AssignmentActivityCruiseResponse(
                entity.getId(),
                entity.getTourId(),
                entity.getCruiseAreaId(),

                entity.getActivityCruiseTourId(),
                entity.getActivityCruiseId(),

                entity.getActivityName(),
                entity.getActivityDescription(),

                entity.getStartTime(),
                entity.getEndTime(),

                entity.getMaxPassengers(),
                entity.getPrice(),

                entity.getImageUrl(),
                entity.getStatus(),

                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}