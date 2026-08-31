package com.project.tour.controller.passenger;

import com.project.tour.dto.passenger.*;
import com.project.tour.service.passenger.PassengerCatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/passenger/tours")
public class PassengerCatalogController {
    private final PassengerCatalogService service;

    public PassengerCatalogController(PassengerCatalogService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PassengerTourSummaryResponse>> getOpenTours() {
        return ResponseEntity.ok(service.getOpenTours());
    }

    @GetMapping("/{tourId}")
    public ResponseEntity<PassengerTourDetailResponse> getOpenTour(@PathVariable UUID tourId) {
        return ResponseEntity.ok(service.getOpenTour(tourId));
    }

    @GetMapping("/{tourId}/departures")
    public ResponseEntity<List<PassengerDepartureResponse>> getDepartures(@PathVariable UUID tourId) {
        return ResponseEntity.ok(service.getDepartures(tourId));
    }
}
