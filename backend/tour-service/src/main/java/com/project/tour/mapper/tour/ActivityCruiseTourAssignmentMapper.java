package com.project.tour.mapper.tour;

import com.project.tour.dto.tour.operation.ActivityCruiseTourAssignmentResponse;
import com.project.tour.model.ActivityCruiseTour;

public final class ActivityCruiseTourAssignmentMapper {

        private ActivityCruiseTourAssignmentMapper() {
        }

        public static ActivityCruiseTourAssignmentResponse toResponse(
                        ActivityCruiseTour assignment) {

                var tour = assignment.getTour();
                var cruiseArea = assignment.getCruiseArea();

                var cruiseDeck = cruiseArea != null
                                ? cruiseArea.getCruiseDeck()
                                : null;

                var activityCruise = assignment.getActivityCruise();

                return new ActivityCruiseTourAssignmentResponse(

                                assignment.getId(),

                                tour != null ? tour.getId() : null,
                                tour != null ? tour.getCode() : null,
                                tour != null ? tour.getName() : null,

                                activityCruise != null
                                                ? activityCruise.getId()
                                                : null,

                                activityCruise != null
                                                ? activityCruise.getName()
                                                : null,

                                cruiseArea != null
                                                ? cruiseArea.getId()
                                                : null,

                                cruiseArea != null
                                                ? cruiseArea.getName()
                                                : null,

                                cruiseDeck != null
                                                ? cruiseDeck.getId()
                                                : null,

                                cruiseDeck != null
                                                ? cruiseDeck.getDeckNumber()
                                                : null,

                                assignment.getStartTime(),
                                assignment.getEndTime(),
                                assignment.getMaxPassengers(),
                                assignment.getPrice(),
                                assignment.getStatus(),
                                assignment.getCreatedAt(),
                                assignment.getUpdatedAt());
        }
}