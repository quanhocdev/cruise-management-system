package com.project.booking.dto.booking;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingPassengerInfoResponse(
        Long id,
        String fullName,
        String gender,
        String phoneNumber,
        String email,
        UUID roomId,
        String checkinStatus,
        LocalDateTime checkedInAt) {
}