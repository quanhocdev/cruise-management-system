package com.project.tour.dto.internal;

import java.math.BigDecimal;
import java.util.UUID;

public record TourPriceSearchResponse(
        UUID tourId,
        String tourName,
        BigDecimal lowestPrice) {
}