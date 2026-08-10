package com.project.tour.dto.roomtype;

import java.util.UUID;

public record RoomTypeResponse(
    UUID id,
    String name,
    String description
) {
}
