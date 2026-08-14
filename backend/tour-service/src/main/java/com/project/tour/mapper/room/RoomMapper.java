package com.project.tour.mapper.room;

import com.project.tour.dto.room.RoomResponse;
import com.project.tour.dto.room.UpdateRoomRequest;
import com.project.tour.model.CruiseDeck;
import com.project.tour.model.Room;
import com.project.tour.model.RoomType;

public class RoomMapper {

    private RoomMapper() {
    }

    public static Room toEntity(
            CruiseDeck cruiseDeck,
            RoomType roomType,
            String code) {

        Room room = new Room();

        room.setCruiseDeck(cruiseDeck);
        room.setCode(code);
        room.setRoomType(roomType);

        return room;
    }

    public static void updateEntity(
            Room room,
            UpdateRoomRequest request,
            RoomType roomType) {

        room.setCode(request.code());
        room.setRoomType(roomType);
        room.setStatus(request.status());
    }

    public static RoomResponse toResponse(Room room) {

        return new RoomResponse(
                room.getId(),
                room.getCruiseDeck().getId(),
                room.getCode(),
                room.getRoomType().getId(),
                room.getRoomType().getName(),
                room.getStatus());
    }
}