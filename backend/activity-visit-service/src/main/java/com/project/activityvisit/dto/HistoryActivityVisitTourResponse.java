package com.project.activityvisit.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record HistoryActivityVisitTourResponse(
        UUID id,
        UUID tourId,
        Integer totalConfigurations,
        LocalDateTime completedAt) {
}