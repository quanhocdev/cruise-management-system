package com.project.booking.dto.finance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CheckoutPreviewResponse(
        Long bookingId,
        String bookingCode,
        String primaryContactName,
        String primaryContactEmail,
        List<PassengerCheckoutPreview> passengers,
        BigDecimal grandTotal) {

    public record PassengerCheckoutPreview(
            Long bookingPassengerId,
            Long passengerId,
            List<UsageItem> activityVisitUsages,
            List<UsageItem> activityCruiseUsages,
            List<UsageItem> serviceUsages,
            List<UsageItem> productUsages,
            BigDecimal passengerTotal) {
    }

    public record UsageItem(
            Long usageId,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal discountAmount,
            BigDecimal finalAmount,
            LocalDateTime usedAt) {
    }
}