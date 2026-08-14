package com.project.tour.dto.cruise.deck;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateCruiseDeckRequest {

    @NotNull(message = "Total decks is required")
    @Positive(message = "Total decks must be greater than 0")
    private Integer totalDecks;

    public CreateCruiseDeckRequest() {
    }

    public Integer getTotalDecks() {
        return totalDecks;
    }

    public void setTotalDecks(Integer totalDecks) {
        this.totalDecks = totalDecks;
    }
}