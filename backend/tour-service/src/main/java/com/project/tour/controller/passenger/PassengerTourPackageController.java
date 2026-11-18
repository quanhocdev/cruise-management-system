
package com.project.tour.controller.passenger;

import com.project.tour.dto.passenger.tourpackage.PassengerTourPackageDTO;
import com.project.tour.service.passenger.PassengerTourPackageService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/passenger/tours")
public class PassengerTourPackageController {

    private final PassengerTourPackageService service;

    public PassengerTourPackageController(
            PassengerTourPackageService service) {
        this.service = service;
    }

    @GetMapping("/{tourId}/packages")
    public ResponseEntity<List<PassengerTourPackageDTO>> getPackages(
            @PathVariable UUID tourId) {
        return ResponseEntity.ok(service.getPackages(tourId));
    }
}
