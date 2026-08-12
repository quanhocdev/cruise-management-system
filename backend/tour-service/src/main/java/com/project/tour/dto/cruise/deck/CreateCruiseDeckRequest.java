package com.project.tour.dto.cruise.deck;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateCruiseDeckRequest(

                @NotNull(message = "Deck number is required") @Positive(message = "Deck number must be greater than 0") Integer deckNumber) {
}