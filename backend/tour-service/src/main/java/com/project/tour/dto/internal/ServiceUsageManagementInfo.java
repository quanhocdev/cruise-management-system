package com.project.tour.dto.internal;

import com.project.tour.model.convenience.enums.ServiceTourStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record ServiceUsageManagementInfo(
        UUID serviceTourId,
        UUID tourId,
        UUID serviceId,
        String serviceName,
        BigDecimal unitPrice,
        ServiceTourStatus serviceTourStatus,
        boolean serviceActive,
        Integer maxPassengers,
        Integer durationMinutes) {
}