package com.project.tour.mapper.room;

import com.project.tour.dto.room.CreateRoomRequest;
import com.project.tour.dto.room.RoomResponse;
import com.project.tour.dto.room.UpdateRoomRequest;
import com.project.tour.model.Room;
import com.project.tour.model.RoomType;

public class RoomMapper {

    public static Room toEntity(
            CreateRoomRequest request,
            RoomType roomType) {

        Room room = new Room();

        room.setCode(request.getCode());
        room.setRoomType(roomType);

        return room;
    }

    public static void updateEntity(
            Room room,
            UpdateRoomRequest request,
            RoomType roomType) {

        room.setCode(request.getCode());
        room.setRoomType(roomType);
        room.setStatus(request.getStatus());
    }

    public static RoomResponse toResponse(Room room) {

        RoomResponse response = new RoomResponse();

        response.setId(room.getId());
        response.setCruiseAreaId(
                room.getCruiseArea().getId());
        response.setCode(room.getCode());
        response.setRoomTypeId(
                room.getRoomType().getId());
        response.setRoomTypeName(
                room.getRoomType().getName());
        response.setStatus(room.getStatus());

        return response;
    }
}