package com.project.tour.dto.tour.operation;

import java.util.UUID;

public record AvailableCruiseResponse(
        UUID id,
        String name) {
}