package com.project.tour.dto.passenger;

import java.time.LocalDate;
import java.util.UUID;

public record PassengerVoyageBookingContext(
    UUID voyageId, Integer capacity, LocalDate startDate, String status
) {}
