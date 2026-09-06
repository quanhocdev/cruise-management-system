package com.project.booking.dto.booking;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingPassengerResponse(
        Long id,
        Long passengerId,
        Long bookingId,
        UUID roomId,
        String checkinStatus,
        LocalDateTime checkedInAt) {
}