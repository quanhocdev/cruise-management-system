package com.project.tour.controller.passenger;

import com.project.tour.dto.passenger.roomtype.PassengerRoomTypeDetailResponse;
import com.project.tour.service.passenger.PassengerRoomTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/passenger/room-types")
public class PassengerRoomTypeController {

    private final PassengerRoomTypeService passengerRoomTypeService;

    public PassengerRoomTypeController(PassengerRoomTypeService passengerRoomTypeService) {
        this.passengerRoomTypeService = passengerRoomTypeService;
    }

    @GetMapping("/{roomTypeId}")
    public ResponseEntity<PassengerRoomTypeDetailResponse> getRoomTypeDetail(
            @PathVariable UUID roomTypeId) {
        return ResponseEntity.ok(
                passengerRoomTypeService.getRoomTypeDetail(roomTypeId));
    }
}