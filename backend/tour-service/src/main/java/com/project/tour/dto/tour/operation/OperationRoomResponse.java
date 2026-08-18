package com.project.tour.dto.tour.operation;

import com.project.tour.model.enums.RoomStatus;

import java.util.UUID;

public record OperationRoomResponse(
        UUID id,
        String code,
        RoomStatus status) {
}