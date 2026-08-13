package com.project.tour.controller.room;

import com.project.tour.dto.room.CreateRoomRequest;
import com.project.tour.dto.room.RoomResponse;
import com.project.tour.dto.room.UpdateRoomRequest;
import com.project.tour.service.room.RoomService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/decks/{deckId}/rooms")
public class RoomController {

        private final RoomService roomService;

        public RoomController(RoomService roomService) {
                this.roomService = roomService;
        }

        @PostMapping
        public ResponseEntity<RoomResponse> createRoom(
                        @PathVariable UUID deckId,
                        @Valid @RequestBody CreateRoomRequest request) {

                RoomResponse response = roomService.createRoom(
                                deckId,
                                request);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(response);
        }

        @GetMapping
        public ResponseEntity<List<RoomResponse>> getRooms(
                        @PathVariable UUID deckId,
                        @RequestParam(defaultValue = "false") boolean activeOnly) {

                List<RoomResponse> response = activeOnly
                                ? roomService.getActiveRoomsByDeck(deckId)
                                : roomService.getRoomsByDeck(deckId);

                return ResponseEntity.ok(response);
        }

        @GetMapping("/{roomId}")
        public ResponseEntity<RoomResponse> getRoomById(
                        @PathVariable UUID deckId,
                        @PathVariable UUID roomId) {

                return ResponseEntity.ok(
                                roomService.getRoomById(
                                                deckId,
                                                roomId));
        }

        @PatchMapping("/{roomId}")
        public ResponseEntity<RoomResponse> updateRoom(
                        @PathVariable UUID deckId,
                        @PathVariable UUID roomId,
                        @Valid @RequestBody UpdateRoomRequest request) {

                return ResponseEntity.ok(
                                roomService.updateRoom(
                                                deckId,
                                                roomId,
                                                request));
        }

        @DeleteMapping("/{roomId}")
        public ResponseEntity<Void> deleteRoom(
                        @PathVariable UUID deckId,
                        @PathVariable UUID roomId) {

                roomService.deleteRoom(
                                deckId,
                                roomId);

                return ResponseEntity
                                .noContent()
                                .build();
        }
}
