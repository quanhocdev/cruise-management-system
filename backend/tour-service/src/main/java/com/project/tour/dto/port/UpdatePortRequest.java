package com.project.tour.dto.port;

import com.project.tour.model.enums.PortStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class UpdatePortRequest {

    @NotBlank(message = "Port name is required")
    @Size(max = 150, message = "Port name must not exceed 150 characters")
    private String name;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be greater or equal to -90")
    @DecimalMax(value = "90.0", message = "Latitude must be less or equal to 90")
    private BigDecimal latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be greater or equal to -180")
    @DecimalMax(value = "180.0", message = "Longitude must be less or equal to 180")
    private BigDecimal longitude;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Port status is required")
    private PortStatus status;

    public UpdatePortRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PortStatus getStatus() {
        return status;
    }

    public void setStatus(PortStatus status) {
        this.status = status;
    }
}