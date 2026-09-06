package com.project.booking.dto.passenger;

import com.project.booking.model.enums.DocumentType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PassengerResponse(
        Long id,
        Long userId,
        String fullName,
        LocalDate dateOfBirth,
        String gender,
        String phoneNumber,
        String email,
        DocumentType idCardType,
        String identificationNumber,
        String documentNote,
        String idCardImageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}