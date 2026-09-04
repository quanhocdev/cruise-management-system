package com.project.convenience.dto.product.convenience;

import java.time.LocalDateTime;
import java.util.UUID;

public record HistoryProductTourResponse(

        UUID id,

        UUID tourId,

        Integer totalConfigurations,

        LocalDateTime completedAt) {
}