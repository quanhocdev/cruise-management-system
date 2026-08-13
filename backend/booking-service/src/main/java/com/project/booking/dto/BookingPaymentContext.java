package com.project.booking.dto;

import com.project.booking.model.enums.BookingStatus;
import java.math.BigDecimal;

public record BookingPaymentContext(
    Long bookingId, Long userId, BigDecimal totalAmount, BookingStatus status
) {}
