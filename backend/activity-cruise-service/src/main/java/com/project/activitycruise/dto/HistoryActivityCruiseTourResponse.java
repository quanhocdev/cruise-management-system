package com.project.activitycruise.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record HistoryActivityCruiseTourResponse(
        UUID id,
        UUID tourId,
        Integer totalConfigurations,
        LocalDateTime completedAt) {
}