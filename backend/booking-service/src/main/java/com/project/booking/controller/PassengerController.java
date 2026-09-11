package com.project.booking.controller;

import com.project.booking.dto.passenger.PassengerRequest;
import com.project.booking.dto.passenger.PassengerResponse;
import com.project.booking.service.PassengerService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/passengers")
public class PassengerController {

    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @GetMapping
    public ResponseEntity<List<PassengerResponse>> getAll(@RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(passengerService.getAllPassengers(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(passengerService.getPassengerById(id));
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PassengerResponse> update(@PathVariable Long id,
            @Valid @ModelAttribute PassengerRequest request) {
        return ResponseEntity.ok(passengerService.updatePassenger(id, request));
    }
}