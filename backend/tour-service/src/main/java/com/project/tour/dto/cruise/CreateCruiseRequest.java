package com.project.tour.dto.cruise;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateCruiseRequest(

    @NotBlank(message = "Cruise name is required")
    @Size(
        max = 150,
        message = "Cruise name must not exceed 150 characters"
    )
    String name,

    @NotBlank(message = "Cruise code is required")
    @Size(
        max = 50,
        message = "Cruise code must not exceed 50 characters"
    )
    @Pattern(
        regexp = "^[A-Za-z0-9_-]+$",
        message = "Cruise code may only contain letters, numbers, hyphens and underscores"
    )
    String code,

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
    Integer maxPassengers
) {
}