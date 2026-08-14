package com.project.booking.dto;

import com.project.booking.model.enums.BookingStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.List;

public record BookingResponse(
    Long id, UUID voyageId, String bookingCode, Long createdByUserId,
    String primaryContactName, String primaryContactPhone,
    BigDecimal totalAmount, BookingStatus status, Long paymentId,
    Instant createdAt, Instant updatedAt, List<PassengerVoyageResponse> passengers
) {}
