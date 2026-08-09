package com.project.tour.dto.cruise;

import com.project.tour.model.enums.CruiseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateCruiseRequest(

    @NotBlank(message = "Cruise name is required")
    @Size(
        max = 150,
        message = "Cruise name must not exceed 150 characters"
    )
    String name,

    @Size(
        max = 5000,
        message = "Description must not exceed 5000 characters"
    )
    String description,

    @NotNull(message = "Total decks is required")
    @Positive(message = "Total decks must be greater than 0")
    Integer totalDecks,

    @NotNull(message = "Maximum passengers is required")
    @Positive(message = "Maximum passengers must be greater than 0")
    Integer maxPassengers,

    @NotNull(message = "Cruise status is required")
    CruiseStatus status
) {
}