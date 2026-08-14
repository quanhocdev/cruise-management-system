package com.project.tour.dto.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateRoomRequest(

        @NotBlank(message = "Room code is required") @Size(max = 50, message = "Room code must not exceed 50 characters") @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Room code may only contain letters, numbers, hyphens and underscores") String code,

        @NotNull(message = "Room type id is required") UUID roomTypeId

) {
}
