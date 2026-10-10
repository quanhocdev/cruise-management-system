package com.project.booking.dto.convenience.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ServiceUsageManagementResponse(
        Long id,
        Long bookingPassengerId,
        UUID serviceTourId,
        UUID tourId,
        UUID serviceId,
        String serviceName,
        BigDecimal unitPrice,
        BigDecimal discountAmount,
        BigDecimal finalAmount,
        String serviceTourStatus,
        boolean serviceActive,
        Integer maxPassengers,
        Integer durationMinutes,
        LocalDateTime usedAt,
        LocalDateTime expiresAt,
        LocalDateTime endedAt) {
}
