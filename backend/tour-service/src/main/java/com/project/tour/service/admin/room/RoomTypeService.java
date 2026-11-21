package com.project.tour.service.admin.room;

import com.project.common.dto.UploadResult;
import com.project.common.service.file.FileStorageService;
import com.project.tour.dto.admin.roomtype.CreateRoomTypeRequest;
import com.project.tour.dto.admin.roomtype.RoomTypeResponse;
import com.project.tour.dto.admin.roomtype.UpdateRoomTypeRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.admin.room.RoomTypeMapper;
import com.project.tour.model.admin.RoomType;
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
    private final FileStorageService fileStorageService;

    public RoomTypeService(
            RoomTypeRepository roomTypeRepository,
            RoomRepository roomRepository,
            FileStorageService fileStorageService) {

        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
        this.fileStorageService = fileStorageService;
    }

    public RoomTypeResponse createRoomType(CreateRoomTypeRequest request) {

        if (roomTypeRepository.existsByNameIgnoreCase(request.name())) {
            throw new AppException(
                    "Room type name already exists",
                    HttpStatus.CONFLICT);
        }

        RoomType roomType = RoomTypeMapper.toEntity(request);

        if (request.image() != null && !request.image().isEmpty()) {
            UploadResult uploadResult = fileStorageService.saveMultipart(
                    request.image(),
                    "room-types");

            roomType.setImageUrl(uploadResult.getUrl());
            roomType.setImagePublicId(uploadResult.getPublicId());
        }

        RoomType savedRoomType = roomTypeRepository.save(roomType);

        return RoomTypeMapper.toResponse(savedRoomType);
    }

    @Transactional(readOnly = true)
    public RoomTypeResponse getRoomTypeById(UUID id) {
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

    public RoomTypeResponse updateRoomType(UUID id, UpdateRoomTypeRequest request) {

        RoomType roomType = findById(id);

        if (roomTypeRepository.existsByNameIgnoreCaseAndIdNot(request.name(), id)) {
            throw new AppException(
                    "Room type name already exists",
                    HttpStatus.CONFLICT);
        }

        String oldPublicId = roomType.getImagePublicId();

        RoomTypeMapper.updateEntity(roomType, request);

        if (request.image() != null && !request.image().isEmpty()) {
            UploadResult uploadResult = fileStorageService.saveMultipart(
                    request.image(),
                    "room-types");

            roomType.setImageUrl(uploadResult.getUrl());
            roomType.setImagePublicId(uploadResult.getPublicId());

            if (oldPublicId != null && !oldPublicId.isBlank()) {
                fileStorageService.delete(oldPublicId);
            }
        }

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

        if (roomType.getImagePublicId() != null && !roomType.getImagePublicId().isBlank()) {
            fileStorageService.delete(roomType.getImagePublicId());
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