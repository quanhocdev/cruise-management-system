package com.project.tour.dto.operation.assignment.room;

import com.project.tour.model.enums.RoomStatus;

import java.util.UUID;

public record OperationRoomResponse(
                UUID id,
                String code,
                RoomStatus status) {
}