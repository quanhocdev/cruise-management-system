package com.project.tour.service;

import com.project.tour.dto.roomtype.CreateRoomTypeRequest;
import com.project.tour.dto.roomtype.RoomTypeResponse;
import com.project.tour.dto.roomtype.UpdateRoomTypeRequest;
import com.project.tour.exception.DuplicateResourceException;
import com.project.tour.exception.ResourceNotFoundException;
import com.project.tour.model.RoomType;
import com.project.tour.repository.RoomTypeRepository;
import com.project.tour.repository.RoomRepository;
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
        RoomRepository roomRepository
    ) {
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
    }

    public RoomTypeResponse createRoomType(CreateRoomTypeRequest request) {
        String name = request.name().trim();

        if (roomTypeRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException(
                "Room type name already exists: " + name
            );
        }

        RoomType roomType = new RoomType();
        roomType.setName(name);
        roomType.setDescription(trimToNull(request.description()));

        return toResponse(roomTypeRepository.save(roomType));
    }

    @Transactional(readOnly = true)
    public RoomTypeResponse getRoomTypeById(UUID id) {
        return toResponse(findRoomTypeById(id));
    }

    @Transactional(readOnly = true)
    public List<RoomTypeResponse> getAllRoomTypes() {
        return roomTypeRepository.findAllByOrderByNameAsc()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public RoomTypeResponse updateRoomType(
        UUID id,
        UpdateRoomTypeRequest request
    ) {
        RoomType roomType = findRoomTypeById(id);
        String name = request.name().trim();

        if (roomTypeRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new DuplicateResourceException(
                "Room type name already exists: " + name
            );
        }

        roomType.setName(name);
        roomType.setDescription(trimToNull(request.description()));

        return toResponse(roomTypeRepository.save(roomType));
    }

    public void deleteRoomType(UUID id) {
        RoomType roomType = findRoomTypeById(id);

        if (roomRepository.existsByRoomType_Id(id)) {
            throw new IllegalArgumentException(
                "Room type cannot be deleted because it is used by rooms"
            );
        }

        roomTypeRepository.delete(roomType);
    }

    private RoomType findRoomTypeById(UUID id) {
        return roomTypeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Room type not found with id: " + id
            ));
    }

    private RoomTypeResponse toResponse(RoomType roomType) {
        return new RoomTypeResponse(
            roomType.getId(),
            roomType.getName(),
            roomType.getDescription()
        );
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
