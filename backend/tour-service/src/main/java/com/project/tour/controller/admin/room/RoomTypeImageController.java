package com.project.tour.controller.admin.room;

import com.project.tour.dto.admin.roomtype.CreateRoomTypeImageRequest;
import com.project.tour.dto.admin.roomtype.RoomTypeImageResponse;
import com.project.tour.service.admin.room.RoomTypeImageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/room-types/{roomTypeId}/images")
public class RoomTypeImageController {

    private final RoomTypeImageService roomTypeImageService;

    public RoomTypeImageController(RoomTypeImageService roomTypeImageService) {
        this.roomTypeImageService = roomTypeImageService;
    }

    @GetMapping
    public ResponseEntity<List<RoomTypeImageResponse>> getImages(
            @PathVariable UUID roomTypeId) {

        return ResponseEntity.ok(
                roomTypeImageService.getImagesByRoomType(roomTypeId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RoomTypeImageResponse> addImage(
            @PathVariable UUID roomTypeId,
            @Valid @ModelAttribute CreateRoomTypeImageRequest request) {

        RoomTypeImageResponse response = roomTypeImageService.addImage(roomTypeId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable UUID roomTypeId,
            @PathVariable UUID imageId) {

        roomTypeImageService.deleteImage(roomTypeId, imageId);

        return ResponseEntity.noContent().build();
    }
}