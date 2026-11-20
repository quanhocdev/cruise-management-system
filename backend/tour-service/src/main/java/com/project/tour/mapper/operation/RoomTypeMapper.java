package com.project.tour.mapper.operation;

import com.project.tour.dto.admin.roomtype.RoomTypeResponse;
import com.project.tour.model.admin.RoomType;

public class RoomTypeMapper {

    private RoomTypeMapper() {
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