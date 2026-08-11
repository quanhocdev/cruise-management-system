package com.project.tour.dto.itinerary;

import java.time.LocalDate;
import java.util.UUID;

public record ItineraryDayResponse(
    UUID id, UUID scheduleId, Integer dayNumber,
    LocalDate itineraryDate, String title, String description
) {}
