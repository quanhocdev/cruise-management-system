package com.project.tour.mapper.tour.operation;

import com.project.tour.dto.tour.operation.AssignmentActivityCruiseResponse;
import com.project.tour.model.activitycruise.ActivityCruiseTour;

public final class AssignmentActivityCruiseMapper {

    private AssignmentActivityCruiseMapper() {
    }

    public static AssignmentActivityCruiseResponse toResponse(
            ActivityCruiseTour entity) {

        if (entity == null) {
            return null;
        }

        return new AssignmentActivityCruiseResponse(
                entity.getId(),

                entity.getTourId(),
                entity.getCruiseAreaId(),

                // Không còn activityCruiseTourId riêng.
                // id của chính ActivityCruiseTour là ID assignment/config.
                entity.getId(),

                entity.getActivityCruise() != null
                        ? entity.getActivityCruise().getId()
                        : null,

                // Snapshot data
                entity.getActivityName(),
                entity.getActivityDescription(),

                entity.getStartTime(),
                entity.getEndTime(),

                entity.getMaxPassengers(),
                entity.getPrice(),

                entity.getImageUrl(),

                entity.getStatus() != null
                        ? entity.getStatus().name()
                        : null,

                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
