package com.project.tour.dto.tour;

import com.project.tour.model.enums.tour.TourBookingStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PublicTourSummaryResponse(
        UUID id,
        String code,
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        String cruiseName,
        String cruiseImageUrl,
        TourBookingStatus statusBooking,
        LocalDateTime bookingStart,
        LocalDateTime bookingEnd,
        BigDecimal startingPrice) {
}