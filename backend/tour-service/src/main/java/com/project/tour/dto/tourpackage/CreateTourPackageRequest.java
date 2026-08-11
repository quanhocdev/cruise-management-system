package com.project.tour.dto.tourpackage;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTourPackageRequest(
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
    String description
) {}
