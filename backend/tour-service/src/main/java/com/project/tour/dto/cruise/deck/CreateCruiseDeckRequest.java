package com.project.tour.dto.cruise.deck;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateCruiseDeckRequest {

    @NotNull(message = "Deck number is required")
    @Positive(message = "Deck number must be greater than 0")
    private Integer deckNumber;

    public CreateCruiseDeckRequest() {
    }

    public Integer getDeckNumber() {
        return deckNumber;
    }

    public void setDeckNumber(Integer deckNumber) {
        this.deckNumber = deckNumber;
    }
}