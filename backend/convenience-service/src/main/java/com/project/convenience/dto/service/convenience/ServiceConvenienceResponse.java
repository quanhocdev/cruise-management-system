package com.project.convenience.dto.service.convenience;

import java.math.BigDecimal;
import java.util.UUID;

public record ServiceConvenienceResponse(
                UUID id,
                String name,
                String description,
                BigDecimal price,
                Integer durationMinutes,
                Integer maxPassengers,
                String imageUrl) {
}