package com.project.tour.dto.internal;

import java.math.BigDecimal;
import java.util.UUID;

public record BookingPackageInfo(
        UUID packageId,
        BigDecimal price,
        Integer roomCapacity) {
}