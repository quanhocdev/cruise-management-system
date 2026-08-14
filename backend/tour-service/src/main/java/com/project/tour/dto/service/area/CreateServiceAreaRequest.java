package com.project.tour.dto.service.area;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateServiceAreaRequest {

    @NotNull(message = "Service is required")
    private UUID serviceId;

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }
}