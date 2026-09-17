package com.project.booking.dto.booking;

import com.project.booking.model.enums.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookingSummaryResponse(
        Long id,
        UUID tourId,
        String bookingCode,
        String primaryContactName,
        String primaryContactPhone,
        String primaryContactEmail,
        Integer numberPassengers,
        Integer numberOfRooms,
        BigDecimal totalAmount,
        BookingStatus status,
        LocalDateTime createdAt) {
}