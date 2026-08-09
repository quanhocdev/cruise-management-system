package com.project.tour.dto.port;

import com.project.tour.model.enums.PortStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PortResponse(
    UUID id,
    String name,
    String city,
    String country,
    String address,
    BigDecimal latitude,
    BigDecimal longitude,
    String description,
    PortStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
