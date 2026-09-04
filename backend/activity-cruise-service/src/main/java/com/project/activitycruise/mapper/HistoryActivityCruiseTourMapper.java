package com.project.activitycruise.mapper;

import com.project.activitycruise.dto.HistoryActivityCruiseTourResponse;
import com.project.activitycruise.model.HistoryActivityCruiseTour;
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