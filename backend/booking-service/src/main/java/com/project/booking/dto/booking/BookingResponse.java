package com.project.booking.dto.booking;

import com.project.booking.model.enums.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BookingResponse(
        Long id,
        Long createdByUserId,
        UUID tourId,
        UUID tourPackageId,
        String bookingCode,
        Integer numberPassengers,
        String primaryContactName,
        String primaryContactPhone,
        BigDecimal totalAmount,
        BookingStatus status,
        List<BookingPassengerInfoResponse> bookingPassengers,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}