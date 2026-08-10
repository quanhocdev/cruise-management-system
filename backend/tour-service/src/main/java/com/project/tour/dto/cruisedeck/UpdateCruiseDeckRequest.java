package com.project.tour.dto.cruisedeck;

import com.project.tour.model.enums.CruiseDeckStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateCruiseDeckRequest(

    @NotNull(message = "Deck number is required")
    @Positive(message = "Deck number must be greater than 0")
    Integer deckNumber,

    @NotNull(message = "Cruise deck status is required")
    CruiseDeckStatus status
) {
}