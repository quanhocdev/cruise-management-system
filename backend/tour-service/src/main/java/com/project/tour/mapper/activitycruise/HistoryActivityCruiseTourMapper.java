package com.project.tour.mapper.activitycruise;

import com.project.tour.dto.activitycruise.HistoryActivityCruiseTourResponse;
import com.project.tour.model.activitycruise.HistoryActivityCruiseTour;

import org.springframework.stereotype.Component;

@Component
public class HistoryActivityCruiseTourMapper {

    public HistoryActivityCruiseTourResponse toResponse(
            HistoryActivityCruiseTour history) {

        if (history == null) {
            return null;
        }

        return new HistoryActivityCruiseTourResponse(
                history.getId(),
                history.getTourId(),
                history.getTotalConfigurations(),
                history.getCompletedAt());
    }
}
