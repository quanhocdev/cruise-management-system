package com.project.tour.mapper.room;

import com.project.tour.dto.roomtype.CreateRoomTypeRequest;
import com.project.tour.dto.roomtype.RoomTypeResponse;
import com.project.tour.dto.roomtype.UpdateRoomTypeRequest;
import com.project.tour.model.RoomType;

public class RoomTypeMapper {

    public static RoomType toEntity(CreateRoomTypeRequest request) {

        RoomType roomType = new RoomType();

        roomType.setName(request.getName());
        roomType.setDescription(request.getDescription());
        if (request.getPrice() != null) roomType.setPrice(request.getPrice());
        if (request.getCapacity() != null) roomType.setCapacity(request.getCapacity());

        return roomType;
    }

    public static void updateEntity(
            RoomType roomType,
            UpdateRoomTypeRequest request) {

        roomType.setName(request.getName());
        roomType.setDescription(request.getDescription());
        if (request.getPrice() != null) roomType.setPrice(request.getPrice());
        if (request.getCapacity() != null) roomType.setCapacity(request.getCapacity());
    }

    public static RoomTypeResponse toResponse(RoomType roomType) {

        RoomTypeResponse response = new RoomTypeResponse();

        response.setId(roomType.getId());
        response.setName(roomType.getName());
        response.setDescription(roomType.getDescription());
        response.setPrice(roomType.getPrice());
        response.setCapacity(roomType.getCapacity());

        return response;
    }
}
