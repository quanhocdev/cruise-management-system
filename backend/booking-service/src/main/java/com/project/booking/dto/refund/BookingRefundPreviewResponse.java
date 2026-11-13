package com.project.booking.dto.refund;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BookingRefundPreviewResponse(
        UUID policyRefundRuleId,
        LocalDate departureDate,
        Integer daysBeforeDeparture,
        BigDecimal originalAmount,
        BigDecimal refundPercentage,
        BigDecimal calculatedRefundAmount) {
}