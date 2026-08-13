package com.project.booking.dto;

import com.project.booking.model.enums.BookingStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BookingResponse(
    Long id, Long userId, UUID scheduleId, UUID roomId, Integer guestCount,
    BigDecimal totalAmount, BookingStatus status, Long paymentId,
    Instant createdAt, Instant updatedAt
) {}
