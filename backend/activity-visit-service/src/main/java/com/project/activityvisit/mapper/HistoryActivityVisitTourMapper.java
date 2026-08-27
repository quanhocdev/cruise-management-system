package com.project.activityvisit.mapper;

import com.project.activityvisit.dto.HistoryActivityVisitTourResponse;
import com.project.activityvisit.model.HistoryActivityVisitTour;
import org.springframework.stereotype.Component;

@Component
public class HistoryActivityVisitTourMapper {

    public HistoryActivityVisitTourResponse toResponse(
            HistoryActivityVisitTour history) {

        if (history == null) {
            return null;
        }

        return new HistoryActivityVisitTourResponse(
                history.getId(),
                history.getTourId(),
                history.getTotalConfigurations(),
                history.getCompletedAt());
    }
}