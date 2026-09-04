package com.project.tour.dto.passenger;

import java.time.LocalDate;
import java.util.UUID;

public record PassengerTourSummaryResponse(
    UUID id, String code, String name, String description,
    LocalDate startDate, LocalDate endDate,
    UUID cruiseId, String cruiseName, String cruiseImageUrl
) {}
