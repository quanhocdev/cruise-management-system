package com.project.tour.mapper.tour.onboard;

import com.project.tour.dto.tour.onboard.OnboardActivityCruiseTourResponse;
import com.project.tour.model.ActivityCruiseTour;

public final class OnboardActivityCruiseTourMapper {

    private OnboardActivityCruiseTourMapper() {
    }

    public static OnboardActivityCruiseTourResponse toResponse(
            ActivityCruiseTour assignment) {

        var tour = assignment.getTour();
        var cruiseArea = assignment.getCruiseArea();

        return new OnboardActivityCruiseTourResponse(

                assignment.getId(),

                tour != null ? tour.getId() : null,
                tour != null ? tour.getCode() : null,
                tour != null ? tour.getName() : null,

                cruiseArea != null ? cruiseArea.getId() : null,
                cruiseArea != null ? cruiseArea.getName() : null,

                assignment.getStatus());
    }
}