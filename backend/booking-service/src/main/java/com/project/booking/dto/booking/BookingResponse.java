package com.project.booking.dto.booking;

import com.project.booking.model.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BookingResponse(
        Long id,
        UUID createdByUserId,

        // Thông tin Tour
        UUID tourId,
        String tourCode,
        String tourName,
        LocalDate departureDate,
        LocalDate endDate,

        // Thông tin gói Tour
        UUID tourPackageId,

        // Thông tin Booking
        String bookingCode,
        Integer numberPassengers,
        String primaryContactName,
        String primaryContactPhone,
        BigDecimal totalAmount,
        BookingStatus status,

        // Danh sách hành khách
        List<BookingPassengerInfoResponse> bookingPassengers,

        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}