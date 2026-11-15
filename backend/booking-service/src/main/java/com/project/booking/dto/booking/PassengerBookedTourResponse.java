package com.project.booking.dto.booking;

import java.time.LocalDate;
import java.util.UUID;

public record PassengerBookedTourResponse(
        UUID tourId,
        String tourCode,
        String tourName,
        LocalDate departureDate,
        LocalDate endDate,
        String statusTrip) {
}