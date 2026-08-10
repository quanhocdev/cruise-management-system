package com.project.tour.controller;

import com.project.tour.dto.room.CreateRoomRequest;
import com.project.tour.dto.room.RoomResponse;
import com.project.tour.dto.room.UpdateRoomRequest;
import com.project.tour.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/decks/{deckId}/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(
        @PathVariable UUID deckId,
        @Valid @RequestBody CreateRoomRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(roomService.createRoom(deckId, request));
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getRooms(
        @PathVariable UUID deckId,
        @RequestParam(defaultValue = "false") boolean activeOnly
    ) {
        return ResponseEntity.ok(roomService.getRooms(deckId, activeOnly));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponse> getRoomById(
        @PathVariable UUID deckId,
        @PathVariable UUID roomId
    ) {
        return ResponseEntity.ok(roomService.getRoomById(deckId, roomId));
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(
        @PathVariable UUID deckId,
        @PathVariable UUID roomId,
        @Valid @RequestBody UpdateRoomRequest request
    ) {
        return ResponseEntity.ok(
            roomService.updateRoom(deckId, roomId, request)
        );
    }

    @PatchMapping("/{roomId}/deactivate")
    public ResponseEntity<RoomResponse> deactivateRoom(
        @PathVariable UUID deckId,
        @PathVariable UUID roomId
    ) {
        return ResponseEntity.ok(roomService.deactivateRoom(deckId, roomId));
    }
}
