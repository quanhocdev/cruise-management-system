package com.project.tour.dto.tour;

import com.project.tour.model.enums.tour.TourBookingStatus;
import com.project.tour.model.enums.tour.TourStatusTrip;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TourResponse(

        UUID id,

        String code,

        String name,

        String description,

        LocalDate startDate,

        LocalDate endDate,

        UUID cruiseId,

        String cruiseName,

        TourStatusTrip statusTrip,

        LocalDateTime bookingStart,

        LocalDateTime bookingEnd,

        TourBookingStatus statusBooking,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}