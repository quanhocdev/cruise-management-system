package com.project.tour.controller.tour;

import com.project.tour.dto.tour.CreateTourRequest;
import com.project.tour.dto.tour.TourResponse;
import com.project.tour.dto.tour.UpdateTourRequest;
import com.project.tour.service.tour.TourService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/scheduler/tours")
public class TourController {

    private final TourService tourService;

    public TourController(
            TourService tourService) {

        this.tourService = tourService;
    }

    // =====================================================
    // CREATE TOUR
    // =====================================================

    @PostMapping
    public ResponseEntity<TourResponse> createTour(
            @Valid @RequestBody CreateTourRequest request) {

        TourResponse response = tourService.createTour(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =====================================================
    // GET BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<TourResponse> getTourById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                tourService.getTourById(id));
    }

    // =====================================================
    // GET BY CODE
    // =====================================================

    @GetMapping("/code/{code}")
    public ResponseEntity<TourResponse> getTourByCode(
            @PathVariable String code) {

        return ResponseEntity.ok(
                tourService.getTourByCode(code));
    }

    // =====================================================
    // GET ALL / BY CRUISE
    // =====================================================

    @GetMapping
    public ResponseEntity<List<TourResponse>> getTours(
            @RequestParam(required = false) UUID cruiseId) {

        List<TourResponse> response;

        if (cruiseId != null) {

            response = tourService.getToursByCruise(cruiseId);

        } else {

            response = tourService.getAllTours();
        }

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // UPDATE
    // =====================================================

    @PatchMapping("/{id}")
    public ResponseEntity<TourResponse> updateTour(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTourRequest request) {

        return ResponseEntity.ok(
                tourService.updateTour(
                        id,
                        request));
    }

    // =====================================================
    // DELETE
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(
            @PathVariable UUID id) {

        tourService.deleteTour(id);

        return ResponseEntity.noContent().build();
    }
}