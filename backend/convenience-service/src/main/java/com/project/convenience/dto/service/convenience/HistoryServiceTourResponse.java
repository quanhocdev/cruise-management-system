package com.project.convenience.dto.service.convenience;

import java.time.LocalDateTime;
import java.util.UUID;

public record HistoryServiceTourResponse(
        UUID id,
        UUID tourId,
        Integer totalConfigurations,
        LocalDateTime completedAt) {
}