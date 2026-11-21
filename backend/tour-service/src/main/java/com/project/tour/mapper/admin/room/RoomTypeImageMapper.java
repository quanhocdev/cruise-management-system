package com.project.tour.mapper.admin.room;

import com.project.tour.dto.admin.roomtype.CreateRoomTypeImageRequest;
import com.project.tour.dto.admin.roomtype.RoomTypeImageResponse;
import com.project.tour.model.admin.RoomType;
import com.project.tour.model.admin.RoomTypeImage;

public class RoomTypeImageMapper {

    private RoomTypeImageMapper() {
    }

    public static RoomTypeImage toEntity(
            CreateRoomTypeImageRequest request,
            RoomType roomType,
            String imageUrl,
            String imagePublicId) {

        RoomTypeImage entity = new RoomTypeImage();
        entity.setRoomType(roomType);
        entity.setImageUrl(imageUrl);
        entity.setImagePublicId(imagePublicId);
        entity.setLabel(request.label() != null ? request.label().trim() : null);
        entity.setSortOrder(request.sortOrder() != null ? request.sortOrder() : 0);
        return entity;
    }

    public static RoomTypeImageResponse toResponse(RoomTypeImage entity) {
        return new RoomTypeImageResponse(
                entity.getId(),
                entity.getImageUrl(),
                entity.getLabel(),
                entity.getSortOrder());
    }
}