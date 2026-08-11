package com.project.tour.controller;

import com.project.tour.dto.roomtype.CreateRoomTypeRequest;
import com.project.tour.dto.roomtype.RoomTypeResponse;
import com.project.tour.dto.roomtype.UpdateRoomTypeRequest;
import com.project.tour.service.RoomTypeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/room-types")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    public RoomTypeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    @PostMapping
    public ResponseEntity<RoomTypeResponse> createRoomType(
        @Valid @RequestBody CreateRoomTypeRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(roomTypeService.createRoomType(request));
    }

    @GetMapping
    public ResponseEntity<List<RoomTypeResponse>> getAllRoomTypes() {
        return ResponseEntity.ok(roomTypeService.getAllRoomTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomTypeResponse> getRoomTypeById(
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(roomTypeService.getRoomTypeById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomTypeResponse> updateRoomType(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateRoomTypeRequest request
    ) {
        return ResponseEntity.ok(roomTypeService.updateRoomType(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoomType(@PathVariable UUID id) {
        roomTypeService.deleteRoomType(id);
        return ResponseEntity.noContent().build();
    }
}
