package com.project.tour.dto.room;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateRoomRequest(

                @NotNull(message = "Room type id is required") UUID roomTypeId,

                @NotNull(message = "Quantity is required") @Positive(message = "Quantity must be greater than 0") Integer quantity

) {
}