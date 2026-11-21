package com.project.tour.service.admin.room;

import com.project.common.dto.UploadResult;
import com.project.common.service.file.FileStorageService;
import com.project.tour.dto.admin.roomtype.CreateRoomTypeImageRequest;
import com.project.tour.dto.admin.roomtype.RoomTypeImageResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.admin.room.RoomTypeImageMapper;
import com.project.tour.model.admin.RoomType;
import com.project.tour.model.admin.RoomTypeImage;
import com.project.tour.repository.room.RoomTypeImageRepository;
import com.project.tour.repository.room.RoomTypeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RoomTypeImageService {

    private static final String FOLDER = "room-types/details";

    private final RoomTypeImageRepository roomTypeImageRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final FileStorageService fileStorageService;

    public RoomTypeImageService(
            RoomTypeImageRepository roomTypeImageRepository,
            RoomTypeRepository roomTypeRepository,
            FileStorageService fileStorageService) {

        this.roomTypeImageRepository = roomTypeImageRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public List<RoomTypeImageResponse> getImagesByRoomType(UUID roomTypeId) {
        findRoomTypeById(roomTypeId);

        return roomTypeImageRepository
                .findAllByRoomType_IdOrderBySortOrderAsc(roomTypeId)
                .stream()
                .map(RoomTypeImageMapper::toResponse)
                .toList();
    }

    public RoomTypeImageResponse addImage(
            UUID roomTypeId,
            CreateRoomTypeImageRequest request) {

        RoomType roomType = findRoomTypeById(roomTypeId);

        String imageUrl = null;
        String imagePublicId = null;

        if (request.image() != null && !request.image().isEmpty()) {
            UploadResult uploadResult = fileStorageService.saveMultipart(
                    request.image(),
                    FOLDER);

            imageUrl = uploadResult.getUrl();
            imagePublicId = uploadResult.getPublicId();
        }

        RoomTypeImage entity = RoomTypeImageMapper.toEntity(
                request,
                roomType,
                imageUrl,
                imagePublicId);

        RoomTypeImage saved = roomTypeImageRepository.save(entity);

        return RoomTypeImageMapper.toResponse(saved);
    }

    public void deleteImage(UUID roomTypeId, UUID imageId) {
        findRoomTypeById(roomTypeId);

        RoomTypeImage image = roomTypeImageRepository
                .findByIdAndRoomType_Id(imageId, roomTypeId)
                .orElseThrow(() -> new AppException(
                        "Room type image not found",
                        HttpStatus.NOT_FOUND));

        if (image.getImagePublicId() != null && !image.getImagePublicId().isBlank()) {
            fileStorageService.delete(image.getImagePublicId());
        }

        roomTypeImageRepository.delete(image);
    }

    private RoomType findRoomTypeById(UUID roomTypeId) {
        return roomTypeRepository
                .findById(roomTypeId)
                .orElseThrow(() -> new AppException(
                        "Room type not found",
                        HttpStatus.NOT_FOUND));
    }
}