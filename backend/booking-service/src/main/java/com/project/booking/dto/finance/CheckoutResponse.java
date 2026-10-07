package com.project.booking.dto.finance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CheckoutResponse(
        Long billId,
        String billCode,
        Long bookingId,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        List<BillItemResponse> items,
        Long paymentId,
        String paymentUrl,
        String paymentStatus) {

    public record BillItemResponse(
            Long id,
            Long bookingPassengerId,
            String usageType,
            Long usageId,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal discountAmount,
            BigDecimal finalAmount) {
    }
}