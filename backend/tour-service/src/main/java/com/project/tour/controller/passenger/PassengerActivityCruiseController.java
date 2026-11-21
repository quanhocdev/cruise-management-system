
package com.project.tour.controller.passenger;

import com.project.tour.dto.passenger.activity.PassengerActivityCruiseDTO;
import com.project.tour.service.passenger.PassengerActivityCruiseService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/passenger/tours")
public class PassengerActivityCruiseController {

    private final PassengerActivityCruiseService service;

    public PassengerActivityCruiseController(
            PassengerActivityCruiseService service) {
        this.service = service;
    }

    @GetMapping("/{tourId}/activities-cruise")
    public ResponseEntity<List<PassengerActivityCruiseDTO>> getActivities(
            @PathVariable UUID tourId) {
        return ResponseEntity.ok(service.getActivities(tourId));
    }
}
