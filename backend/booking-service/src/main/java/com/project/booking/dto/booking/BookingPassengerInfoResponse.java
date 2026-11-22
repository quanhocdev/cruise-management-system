package com.project.booking.dto.booking;

import com.project.booking.model.enums.DocumentType;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookingPassengerInfoResponse(
        Long id,
        String fullName,
        String gender,
        String phoneNumber,
        String email,
        DocumentType idCardType,
        String identificationNumber,
        String documentNote,
        UUID roomId,
        String checkinStatus,
        LocalDateTime checkedInAt) {
}