package com.project.tour.dto.cruisearea;

import com.project.tour.model.enums.CruiseAreaStatus;

import java.util.UUID;

public record CruiseAreaResponse(
    UUID id,
    UUID cruiseDeckId,
    String name,
    String description,
    CruiseAreaStatus status,
    String imageUrl,
    String imagePublicId
) {
}
