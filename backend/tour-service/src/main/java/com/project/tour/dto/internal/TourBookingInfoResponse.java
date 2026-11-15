package com.project.tour.dto.internal;

import com.project.tour.model.enums.tour.TourStatusTrip;

import java.time.LocalDate;
import java.util.UUID;

public record TourBookingInfoResponse(
                UUID tourId,
                String tourCode,
                String tourName,
                LocalDate departureDate,
                LocalDate endDate,
                TourStatusTrip statusTrip) {
}