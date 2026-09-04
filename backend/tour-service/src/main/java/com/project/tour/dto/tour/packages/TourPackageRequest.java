package com.project.tour.dto.tour.packages;

import com.project.tour.model.enums.TourPackageStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record TourPackageRequest(
        UUID tourId,
        UUID roomTypeId,
        String name,
        String description,
        BigDecimal price,
        Integer maxPassengers,
        TourPackageStatus status,
        List<PackageBenefitRequest> benefits) {
}