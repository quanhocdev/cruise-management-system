package com.project.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityVisitUsageResponse(

        Long id,

        Long bookingPassengerId,

        UUID visitTourId,

        Integer quantity,

        BigDecimal unitPrice,

        BigDecimal discountAmount,

        BigDecimal finalAmount,

        LocalDateTime usedAt) {
}