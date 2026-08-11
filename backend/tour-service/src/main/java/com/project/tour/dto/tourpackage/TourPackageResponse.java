package com.project.tour.dto.tourpackage;

import com.project.tour.model.enums.TourPackageStatus;
import java.util.UUID;

public record TourPackageResponse(
    UUID id,
    String name,
    Integer numberOfDays,
    Integer numberOfNights,
    String description,
    TourPackageStatus status
) {}
