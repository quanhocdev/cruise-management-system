package com.project.tour.dto.onboard;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateActivityCruiseRequest(
        @NotNull(message = "Cruise Area ID không được để trống") Long cruiseAreaId,

        @NotBlank(message = "Tên hoạt động không được để trống") String name,

        String description,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer maxPassengers,
        BigDecimal price,
        String status,
        String imageUrl,
        String imagePublicId) {
}