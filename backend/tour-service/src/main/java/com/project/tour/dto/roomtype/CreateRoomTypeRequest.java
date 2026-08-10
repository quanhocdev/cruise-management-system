package com.project.tour.dto.roomtype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRoomTypeRequest(
    @NotBlank(message = "Room type name is required")
    @Size(max = 100, message = "Room type name must not exceed 100 characters")
    String name,

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    String description
) {
}
