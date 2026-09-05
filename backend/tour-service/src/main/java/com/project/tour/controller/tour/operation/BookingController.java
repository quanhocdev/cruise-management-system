package com.project.tour.controller.tour.operation;

import com.project.tour.dto.booking.TourOpenBookingRequest;
import com.project.tour.dto.tour.TourResponse;
import com.project.tour.service.tour.TourBookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/operation/bookings")
public class BookingController {

    private final TourBookingService tourBookingService;

    public BookingController(TourBookingService tourBookingService) {
        this.tourBookingService = tourBookingService;
    }

    @GetMapping("/tours/{id}")
    public ResponseEntity<TourResponse> getBookingConfig(@PathVariable UUID id) {
        TourResponse response = tourBookingService.getBookingConfig(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tours/{id}/open")
    public ResponseEntity<TourResponse> openTourBooking(
            @PathVariable UUID id,
            @Valid @RequestBody TourOpenBookingRequest request) {
        TourResponse response = tourBookingService.openBooking(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/tours/{id}")
    public ResponseEntity<TourResponse> updateTourBooking(
            @PathVariable UUID id,
            @Valid @RequestBody TourOpenBookingRequest request) {
        TourResponse response = tourBookingService.updateBookingConfig(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/tours/{id}")
    public ResponseEntity<TourResponse> deleteTourBooking(@PathVariable UUID id) {
        TourResponse response = tourBookingService.deleteBookingConfig(id);
        return ResponseEntity.ok(response);
    }
}