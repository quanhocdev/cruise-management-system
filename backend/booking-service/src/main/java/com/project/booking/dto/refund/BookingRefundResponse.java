package com.project.booking.dto.refund;

import com.project.booking.model.enums.BookingRefundStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookingRefundResponse(
        Long id,
        Long bookingId,
        UUID requestedByUserId,
        String reason,

        UUID policyRefundRuleId,
        LocalDate departureDate,
        Integer daysBeforeDeparture,

        BigDecimal originalAmount,
        BigDecimal refundPercentage,
        BigDecimal calculatedRefundAmount,

        BookingRefundStatus status,

        UUID reviewedByUserId,
        LocalDateTime reviewedAt,
        String rejectionReason,

        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}