
package com.project.tour.controller.passenger;

import com.project.tour.dto.passenger.itinerary.PassengerScheduleDTO;
import com.project.tour.service.passenger.PassengerItineraryService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/passenger/tours")
public class PassengerItineraryController {

    private final PassengerItineraryService service;

    public PassengerItineraryController(
            PassengerItineraryService service) {
        this.service = service;
    }

    @GetMapping("/{tourId}/itinerary")
    public ResponseEntity<List<PassengerScheduleDTO>> getItinerary(
            @PathVariable UUID tourId) {
        return ResponseEntity.ok(service.getItinerary(tourId));
    }
}
