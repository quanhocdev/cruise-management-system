package com.project.tour.controller.internal;

import com.project.tour.service.tour.TourPackageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/tour-packages")
public class InternalTourPackageController {

    private final TourPackageService tourPackageService;

    public InternalTourPackageController(TourPackageService tourPackageService) {
        this.tourPackageService = tourPackageService;
    }

    @GetMapping("/{tourPackageId}/room-capacity")
    public ResponseEntity<Integer> getPackageRoomCapacity(
            @PathVariable UUID tourPackageId) {

        Integer capacity = tourPackageService.getPackageRoomCapacity(tourPackageId);

        return ResponseEntity.ok(capacity);
    }
}