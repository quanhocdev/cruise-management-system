package com.project.tour.service.passenger;

import com.project.tour.dto.admin.roomtype.RoomTypeImageResponse;
import com.project.tour.dto.passenger.roomtype.PassengerRoomTypeDetailResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.admin.room.RoomTypeImageMapper;
import com.project.tour.model.admin.RoomType;
import com.project.tour.repository.room.RoomTypeImageRepository;
import com.project.tour.repository.room.RoomTypeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PassengerRoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomTypeImageRepository roomTypeImageRepository;

    public PassengerRoomTypeService(
            RoomTypeRepository roomTypeRepository,
            RoomTypeImageRepository roomTypeImageRepository) {
        this.roomTypeRepository = roomTypeRepository;
        this.roomTypeImageRepository = roomTypeImageRepository;
    }

    public PassengerRoomTypeDetailResponse getRoomTypeDetail(UUID roomTypeId) {
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new AppException(
                        "Room type not found",
                        HttpStatus.NOT_FOUND));

        List<RoomTypeImageResponse> detailImages = roomTypeImageRepository
                .findAllByRoomType_IdOrderBySortOrderAsc(roomTypeId)
                .stream()
                .map(RoomTypeImageMapper::toResponse)
                .toList();

        return new PassengerRoomTypeDetailResponse(
                roomType.getId(),
                roomType.getName(),
                roomType.getDescription(),
                roomType.getPrice(),
                roomType.getCapacity(),
                roomType.getImageUrl(),
                detailImages);
    }
}