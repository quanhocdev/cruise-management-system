
package com.project.tour.controller.passenger;

import com.project.tour.dto.passenger.service.PassengerServiceDTO;
import com.project.tour.service.passenger.PassengerServiceService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/passenger/tours")
public class PassengerServiceController {

    private final PassengerServiceService service;

    public PassengerServiceController(PassengerServiceService service) {
        this.service = service;
    }

    @GetMapping("/{tourId}/services")
    public ResponseEntity<List<PassengerServiceDTO>> getServices(
            @PathVariable UUID tourId) {
        return ResponseEntity.ok(service.getServices(tourId));
    }
}
