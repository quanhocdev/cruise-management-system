package com.project.tour.dto.activityvisit;

import java.time.LocalDateTime;
import java.util.UUID;

public record HistoryActivityVisitTourResponse(

        UUID id,

        UUID tourId,

        Integer totalConfigurations,

        LocalDateTime completedAt) {

}
