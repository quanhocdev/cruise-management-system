package com.project.tour.dto.operation.packages;

import com.project.tour.dto.operation.packages.benefit.PackageBenefitRequest;
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
        TourPackageStatus status,
        List<PackageBenefitRequest> benefits) {
}