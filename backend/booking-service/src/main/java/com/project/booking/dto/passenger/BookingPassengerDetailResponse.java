package com.project.booking.dto.passenger;

import com.project.booking.model.enums.BookingPassengerStatus;
import com.project.booking.model.enums.DocumentType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookingPassengerDetailResponse(
        // Thông tin từ bảng booking_passengers
        Long bookingPassengerId,
        UUID roomId,
        BookingPassengerStatus status,
        LocalDateTime checkedInAt,
        LocalDateTime checkedOutAt,
        String nfcCardUid,

        // Thông tin chi tiết cá nhân từ bảng passengers
        Long passengerId,
        String fullName,
        LocalDate dateOfBirth,
        String gender,
        String phoneNumber,
        String email,
        DocumentType idCardType,
        String identificationNumber,
        String documentNote,
        String idCardImageUrl) {
}