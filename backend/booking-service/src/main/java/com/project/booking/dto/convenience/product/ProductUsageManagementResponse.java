package com.project.booking.dto.convenience.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductUsageManagementResponse(
        Long id,
        Long bookingPassengerId,
        UUID productTourId,
        UUID tourId,
        UUID productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal discountAmount,
        BigDecimal finalAmount,
        String productTourStatus,
        boolean productActive,
        LocalDateTime usedAt) {
}
