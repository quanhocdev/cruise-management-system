package com.project.tour.controller;

import com.project.tour.dto.tourpackage.*;
import com.project.tour.service.TourPackageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/packages")
public class TourPackageController {

    private final TourPackageService service;

    public TourPackageController(TourPackageService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TourPackageResponse> create(
        @Valid @RequestBody CreateTourPackageRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<TourPackageResponse>> getAll(
        @RequestParam(defaultValue = "false") boolean activeOnly
    ) {
        return ResponseEntity.ok(service.getAll(activeOnly));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourPackageResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TourPackageResponse> update(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateTourPackageRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<TourPackageResponse> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(service.deactivate(id));
    }
}
