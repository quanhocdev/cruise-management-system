package com.project.tour.mapper.operation;

import com.project.tour.dto.admin.roomtype.RoomTypeResponse;
import com.project.tour.model.admin.RoomType;

public class RoomTypeMapper {

    private RoomTypeMapper() {
    }

    public static RoomTypeResponse toResponse(RoomType roomType) {
        RoomTypeResponse dto = new RoomTypeResponse();
        dto.setId(roomType.getId());
        dto.setName(roomType.getName());
        dto.setDescription(roomType.getDescription());
        return dto;
    }
}