
package com.project.tour.controller.passenger;

import com.project.tour.dto.passenger.route.PassengerTourRouteDTO;
import com.project.tour.service.passenger.PassengerRouteService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/passenger/tours")
public class PassengerRouteController {

    private final PassengerRouteService service;

    public PassengerRouteController(PassengerRouteService service) {
        this.service = service;
    }

    @GetMapping("/{tourId}/route")
    public ResponseEntity<PassengerTourRouteDTO> getRoute(
            @PathVariable UUID tourId) {
        return ResponseEntity.ok(service.getRoute(tourId));
    }
}
