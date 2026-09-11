package com.project.tour.dto.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record TourOpenBookingRequest(
        @NotNull(message = "Thời gian mở booking không được để trống") @Future(message = "Thời gian mở booking phải ở tương lai") LocalDateTime bookingStart,

        @NotNull(message = "Thời gian đóng booking không được để trống") @Future(message = "Thời gian đóng booking phải ở tương lai") LocalDateTime bookingEnd) {
}