package com.project.tour.dto.tourpackage;

import com.project.tour.model.enums.TourPackageStatus;
import jakarta.validation.constraints.*;

public record UpdateTourPackageRequest(
    @NotBlank(message = "Package name is required")
    @Size(max = 200, message = "Package name must not exceed 200 characters")
    String name,

    @NotNull(message = "Number of days is required")
    @Min(value = 1, message = "Number of days must be at least 1")
    Integer numberOfDays,

    @NotNull(message = "Number of nights is required")
    @Min(value = 0, message = "Number of nights must be non-negative")
    Integer numberOfNights,

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    String description,

    @NotNull(message = "Package status is required")
    TourPackageStatus status
) {}
