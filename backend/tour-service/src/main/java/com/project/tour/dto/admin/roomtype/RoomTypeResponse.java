package com.project.tour.dto.admin.roomtype;

import java.math.BigDecimal;
import java.util.UUID;

public record RoomTypeResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer capacity,
        String imageUrl) {
}