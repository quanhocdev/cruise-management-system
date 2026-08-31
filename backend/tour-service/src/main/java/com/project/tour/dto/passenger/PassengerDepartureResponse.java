package com.project.tour.dto.passenger;

import java.time.LocalDate;
import java.util.UUID;

public record PassengerDepartureResponse(
    UUID voyageId, UUID tourId, String tourCode,
    LocalDate departureDate, LocalDate returnDate,
    UUID cruiseId, String cruiseName, Integer capacity, String status
) {}
