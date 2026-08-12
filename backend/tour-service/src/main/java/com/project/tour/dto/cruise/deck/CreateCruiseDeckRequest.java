package com.project.tour.dto.cruise.deck;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateCruiseDeckRequest {

    @NotNull(message = "Cruise ID is required")
    private java.util.UUID cruiseId;

    @NotNull(message = "Deck number is required")
    @Positive(message = "Deck number must be greater than 0")
    private Integer deckNumber;

    public CreateCruiseDeckRequest() {
    }

    public java.util.UUID getCruiseId() {
        return cruiseId;
    }

    public void setCruiseId(java.util.UUID cruiseId) {
        this.cruiseId = cruiseId;
    }

    public Integer getDeckNumber() {
        return deckNumber;
    }

    public void setDeckNumber(Integer deckNumber) {
        this.deckNumber = deckNumber;
    }
}