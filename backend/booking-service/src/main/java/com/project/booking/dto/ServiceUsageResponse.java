package com.project.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ServiceUsageResponse(

        Long id,

        Long bookingPassengerId,

        UUID serviceTourId,

        BigDecimal unitPrice,

        BigDecimal discountAmount,

        BigDecimal finalAmount,

        LocalDateTime usedAt,

        LocalDateTime expiresAt,

        LocalDateTime endedAt

) {
}