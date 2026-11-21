package com.project.tour.mapper.admin.room;

import com.project.tour.dto.admin.roomtype.CreateRoomTypeRequest;
import com.project.tour.dto.admin.roomtype.RoomTypeResponse;
import com.project.tour.dto.admin.roomtype.UpdateRoomTypeRequest;
import com.project.tour.model.admin.RoomType;

public class RoomTypeMapper {

    private RoomTypeMapper() {
    }

    public static RoomType toEntity(CreateRoomTypeRequest request) {
        RoomType roomType = new RoomType();
        roomType.setName(request.name());
        roomType.setDescription(request.description());
        if (request.price() != null) {
            roomType.setPrice(request.price());
        }
        if (request.capacity() != null) {
            roomType.setCapacity(request.capacity());
        }
        return roomType;
    }

    public static void updateEntity(
            RoomType roomType,
            UpdateRoomTypeRequest request) {
        roomType.setName(request.name());
        roomType.setDescription(request.description());
        if (request.price() != null) {
            roomType.setPrice(request.price());
        }
        if (request.capacity() != null) {
            roomType.setCapacity(request.capacity());
        }
    }

    public static RoomTypeResponse toResponse(RoomType roomType) {
        return new RoomTypeResponse(
                roomType.getId(),
                roomType.getName(),
                roomType.getDescription(),
                roomType.getPrice(),
                roomType.getCapacity(),
                roomType.getImageUrl());
    }
}