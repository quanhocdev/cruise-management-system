package com.project.tour.dto.cruisedeck;

import com.project.tour.model.enums.CruiseDeckStatus;

import java.util.UUID;

public record CruiseDeckResponse(
    UUID id,
    UUID cruiseId,
    Integer deckNumber,
    CruiseDeckStatus status
) {
}