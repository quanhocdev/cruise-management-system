
package com.project.booking.dto.passenger.passenger;

import com.project.booking.model.enums.BookingPassengerStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record PassengerBookingPassengerDTO(
        Long id,
        PassengerProfileDTO passenger,
        UUID roomId,
        String nfcCardUid,
        BookingPassengerStatus status,
        LocalDateTime checkedInAt,
        LocalDateTime checkedOutAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
