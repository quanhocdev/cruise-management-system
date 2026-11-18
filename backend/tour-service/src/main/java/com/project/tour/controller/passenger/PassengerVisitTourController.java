
package com.project.tour.controller.passenger;

import com.project.tour.dto.passenger.activity.PassengerVisitTourDTO;
import com.project.tour.service.passenger.PassengerVisitTourService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/passenger/tours")
public class PassengerVisitTourController {

    private final PassengerVisitTourService service;

    public PassengerVisitTourController(PassengerVisitTourService service) {
        this.service = service;
    }

    @GetMapping("/{tourId}/visits")
    public ResponseEntity<List<PassengerVisitTourDTO>> getVisits(
            @PathVariable UUID tourId) {
        return ResponseEntity.ok(service.getVisits(tourId));
    }
}
