package com.project.tour.mapper.tour;

import com.project.tour.dto.tour.operation.ActivityCruiseTourAssignmentResponse;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.CruiseDeck;
import com.project.tour.model.Tour;
import com.project.tour.model.activitycruise.ActivityCruiseTour;

public final class ActivityCruiseTourAssignmentMapper {

        private ActivityCruiseTourAssignmentMapper() {
        }

        public static ActivityCruiseTourAssignmentResponse toResponse(
                        ActivityCruiseTour assignment,
                        Tour tour,
                        CruiseArea cruiseArea) {

                CruiseDeck cruiseDeck = cruiseArea != null
                                ? cruiseArea.getCruiseDeck()
                                : null;

                return new ActivityCruiseTourAssignmentResponse(
                                assignment.getId(),

                                tour != null
                                                ? tour.getId()
                                                : null,

                                tour != null
                                                ? tour.getCode()
                                                : null,

                                tour != null
                                                ? tour.getName()
                                                : null,

                                assignment.getCruiseAreaId(),

                                cruiseArea != null
                                                ? cruiseArea.getName()
                                                : null,

                                cruiseDeck != null
                                                ? cruiseDeck.getId()
                                                : null,

                                cruiseDeck != null
                                                ? cruiseDeck.getDeckNumber()
                                                : null,

                                assignment.getCreatedAt(),
                                assignment.getUpdatedAt());
        }
}