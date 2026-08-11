package com.project.tour.dto.itinerary;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record CreateItineraryDayRequest(
    @NotNull @Min(value = 1, message = "Day number must be at least 1") Integer dayNumber,
    @NotNull(message = "Itinerary date is required") LocalDate itineraryDate,
    @NotBlank(message = "Itinerary title is required")
    @Size(max = 200, message = "Itinerary title must not exceed 200 characters") String title,
    @Size(max = 5000, message = "Description must not exceed 5000 characters") String description
) {}
