package com.project.tour.dto.admin.roomtype;

import java.util.UUID;

public record RoomTypeImageResponse(
        UUID id,
        String imageUrl,
        String label,
        Integer sortOrder) {
}