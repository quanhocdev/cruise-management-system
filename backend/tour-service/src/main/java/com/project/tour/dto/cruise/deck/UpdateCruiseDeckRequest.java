package com.project.tour.dto.cruise.deck;

import com.project.tour.model.enums.cruise.CruiseDeckStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class UpdateCruiseDeckRequest {

    @NotNull(message = "Deck number is required")
    @Positive(message = "Deck number must be greater than 0")
    private Integer deckNumber;

    @NotNull(message = "Deck status is required")
    private CruiseDeckStatus status;

    public UpdateCruiseDeckRequest() {
    }

    public Integer getDeckNumber() {
        return deckNumber;
    }

    public void setDeckNumber(Integer deckNumber) {
        this.deckNumber = deckNumber;
    }

    public CruiseDeckStatus getStatus() {
        return status;
    }

    public void setStatus(CruiseDeckStatus status) {
        this.status = status;
    }
}