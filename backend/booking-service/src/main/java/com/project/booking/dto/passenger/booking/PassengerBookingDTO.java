
package com.project.booking.dto.passenger.booking;

import com.project.booking.model.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PassengerBookingDTO(
        Long id,
        UUID createdByUserId,
        UUID tourId,
        UUID tourPackageId,
        String bookingCode,
        Integer numberPassengers,
        String primaryContactName,
        String primaryContactEmail,
        String primaryContactPhone,
        Integer numberOfRooms,
        BigDecimal totalAmount,
        BookingStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
