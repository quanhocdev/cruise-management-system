
package com.project.booking.dto.passenger.passenger;

import com.project.booking.model.enums.DocumentType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PassengerProfileDTO(
        Long id,
        String fullName,
        LocalDate dateOfBirth,
        String gender,
        String phoneNumber,
        String email,
        DocumentType idCardType,
        String identificationNumber,
        String documentNote,
        String idCardImageUrl,
        String idCardImagePublicId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
