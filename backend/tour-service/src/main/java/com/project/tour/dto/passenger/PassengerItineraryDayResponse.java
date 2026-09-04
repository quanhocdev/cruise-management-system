package com.project.tour.dto.passenger;

import java.time.LocalDate;
import java.util.UUID;

public record PassengerItineraryDayResponse(
    UUID id, Integer dayNumber, LocalDate date, String name, String description
) {}
