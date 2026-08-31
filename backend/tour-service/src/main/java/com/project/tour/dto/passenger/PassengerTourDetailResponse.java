package com.project.tour.dto.passenger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PassengerTourDetailResponse(
    UUID id, String code, String name, String description,
    LocalDate startDate, LocalDate endDate,
    LocalDateTime bookingStart, LocalDateTime bookingEnd,
    UUID cruiseId, String cruiseName, String cruiseDescription,
    String cruiseImageUrl, Integer maxPassengers,
    List<PassengerItineraryDayResponse> itinerary
) {}
