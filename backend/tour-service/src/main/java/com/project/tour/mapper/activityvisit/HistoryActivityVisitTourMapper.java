package com.project.tour.mapper.activityvisit;

import com.project.tour.dto.activityvisit.HistoryActivityVisitTourResponse;
import com.project.tour.model.activityvisit.HistoryActivityVisitTour;
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
