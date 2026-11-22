package com.project.booking.dto.booking;

import com.project.booking.model.enums.DocumentType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookingPassengerResponse(
                Long id,
                Long passengerId,
                Long bookingId,
                UUID roomId,
                String checkinStatus,
                LocalDateTime checkedInAt,
                DocumentType idCardType,
                String identificationNumber,
                String documentNote,
                LocalDate dateOfBirth) {
}