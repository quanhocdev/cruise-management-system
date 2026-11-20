package com.project.tour.dto.passenger.roomtype;

import com.project.tour.dto.admin.roomtype.RoomTypeImageResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PassengerRoomTypeDetailResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer capacity,
        String imageUrl,
        List<RoomTypeImageResponse> detailImages) {
}