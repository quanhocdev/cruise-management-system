package com.project.tour.service.room;

import com.project.tour.dto.roomtype.CreateRoomTypeRequest;
import com.project.tour.dto.roomtype.RoomTypeResponse;
import com.project.tour.dto.roomtype.UpdateRoomTypeRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.room.RoomTypeMapper;
import com.project.tour.model.RoomType;
import com.project.tour.repository.room.RoomRepository;
import com.project.tour.repository.room.RoomTypeRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;

    public RoomTypeService(
            RoomTypeRepository roomTypeRepository,
            RoomRepository roomRepository) {

        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
    }

    public RoomTypeResponse createRoomType(
            CreateRoomTypeRequest request) {

        if (roomTypeRepository.existsByNameIgnoreCase(
                request.getName())) {

            throw new AppException(
                    "Room type name already exists",
                    HttpStatus.CONFLICT);
        }

        RoomType roomType = RoomTypeMapper.toEntity(request);

        RoomType savedRoomType = roomTypeRepository.save(roomType);

        return RoomTypeMapper.toResponse(savedRoomType);
    }

    @Transactional(readOnly = true)
    public RoomTypeResponse getRoomTypeById(
            UUID id) {

        RoomType roomType = findById(id);

        return RoomTypeMapper.toResponse(roomType);
    }

    @Transactional(readOnly = true)
    public List<RoomTypeResponse> getAllRoomTypes() {

        return roomTypeRepository
                .findAllByOrderByNameAsc()
                .stream()
                .map(RoomTypeMapper::toResponse)
                .toList();
    }

    public RoomTypeResponse updateRoomType(
            UUID id,
            UpdateRoomTypeRequest request) {

        RoomType roomType = findById(id);

        if (roomTypeRepository
                .existsByNameIgnoreCaseAndIdNot(
                        request.getName(),
                        id)) {

            throw new AppException(
                    "Room type name already exists",
                    HttpStatus.CONFLICT);
        }

        RoomTypeMapper.updateEntity(
                roomType,
                request);

        RoomType updatedRoomType = roomTypeRepository.save(roomType);

        return RoomTypeMapper.toResponse(updatedRoomType);
    }

    public void deleteRoomType(UUID id) {

        RoomType roomType = findById(id);

        if (roomRepository.existsByRoomType_Id(id)) {

            throw new AppException(
                    "Cannot delete room type because it is being used by a room",
                    HttpStatus.CONFLICT);
        }

        roomTypeRepository.delete(roomType);
    }

    private RoomType findById(UUID id) {

        return roomTypeRepository
                .findById(id)
                .orElseThrow(() -> new AppException(
                        "Room type not found",
                        HttpStatus.NOT_FOUND));
    }
}