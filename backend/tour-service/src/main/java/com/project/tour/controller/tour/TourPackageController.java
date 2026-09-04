package com.project.tour.controller.tour;

import com.project.tour.dto.tour.packages.TourPackageRequest;
import com.project.tour.dto.tour.packages.TourPackageResponse;
import com.project.tour.service.tour.TourPackageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/operation/tour-packages")
public class TourPackageController {

    private final TourPackageService tourPackageService;

    public TourPackageController(TourPackageService tourPackageService) {
        this.tourPackageService = tourPackageService;
    }

    @PostMapping
    public ResponseEntity<TourPackageResponse> createPackage(
            @Valid @RequestBody TourPackageRequest request) {
        TourPackageResponse response = tourPackageService.createPackage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<TourPackageResponse>> getPackagesByTourId(
            @PathVariable UUID tourId) {
        List<TourPackageResponse> responseList = tourPackageService.getPackagesByTourId(tourId);
        return ResponseEntity.ok(responseList);
    }

    @PatchMapping("/{packageId}")
    public ResponseEntity<TourPackageResponse> patchPackage(
            @PathVariable UUID packageId,
            @RequestBody TourPackageRequest request) {
        TourPackageResponse response = tourPackageService.patchPackage(packageId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{packageId}")
    public ResponseEntity<Void> deletePackage(@PathVariable UUID packageId) {
        tourPackageService.deletePackage(packageId);
        return ResponseEntity.noContent().build();
    }
}