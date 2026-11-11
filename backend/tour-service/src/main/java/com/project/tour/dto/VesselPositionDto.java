package com.project.tour.dto;

import java.time.LocalDateTime;

public record VesselPositionDto(
        double latitude,
        double longitude,
        LocalDateTime updatedAt) {
}