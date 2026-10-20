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

    @com.fasterxml.jackson.annotation.JsonProperty("previewVersion")
    public String previewVersion() {
        return com.project.booking.service.command.CanonicalPayload.hash(java.util.Map.of(
                "bookingId", bookingId, "passengers", passengers, "total", grandTotal));
    }

    public record PassengerCheckoutPreview(
            Long bookingPassengerId,
            Long passengerId,
            List<UsageItem> activityVisitUsages,
            List<UsageItem> activityCruiseUsages,
            List<UsageItem> serviceUsages,
            List<UsageItem> productUsages,
            BigDecimal passengerTotal,
            String passengerName) {
    }

    public record UsageItem(
            Long usageId,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal discountAmount,
            BigDecimal finalAmount,
            LocalDateTime usedAt,
            BigDecimal adjustmentAmount) {
    }
}
