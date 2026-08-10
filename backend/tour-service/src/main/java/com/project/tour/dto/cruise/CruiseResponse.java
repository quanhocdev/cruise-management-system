package com.project.tour.dto.cruise;

import com.project.tour.model.enums.CruiseStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record CruiseResponse(
    UUID id,
    String name,
    String code,
    String description,
    Integer totalDecks,
    Integer maxPassengers,
    String imageUrl,
    String imagePublicId,
    CruiseStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}