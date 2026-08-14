package com.project.tour.dto.room;

import com.project.tour.model.enums.RoomStatus;

import java.util.UUID;

public record RoomResponse(

                UUID id,
                UUID cruiseDeckId,
                String code,
                UUID roomTypeId,
                String roomTypeName,
                RoomStatus status

) {
}