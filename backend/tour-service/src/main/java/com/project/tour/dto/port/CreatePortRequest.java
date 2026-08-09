package com.project.tour.dto.port;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.annotation.Native;
import java.math.BigDecimal;

public record CreatePortRequest(
    
    @NotBlank(message = "Port code is required")
    @Size(max = 20, message = "Port code must not exceed 20 characters")
    String code,

    @NotBlank(message = "Port name is required")
    @Size(max = 150, message = "Port name must not exceed 150 characters")
    String name,

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    String city,

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    String country,

    @Size(max = 255, message = "Address must not exceed 255 characters")
    String address,

    @DecimalMin(
        value = "-90.0",
        message = "Latitude must be greater or equal to -90"
    )
    @DecimalMax(
        value = "90.0",
        message = "Latitude must be less or equal to 90"
    )
    BigDecimal Latitude,

    @DecimalMin(
        value = "-180.0",
        message = "Longitude must be greater or equal to -180"
    )
    @DecimalMax(
        value = "180.0",
        message = "Longitude must be less or equal to 180"
    )
    BigDecimal Longitude,

    String description
)   {
}