package com.project.tour.controller.scheduler;

import com.project.tour.dto.scheduler.tour.CreateTourRequest;
import com.project.tour.dto.scheduler.CreateTourWithRouteRequest;
import com.project.tour.dto.scheduler.CreateTourWithRouteResponse;
import com.project.tour.dto.scheduler.tour.TourResponse;
import com.project.tour.dto.scheduler.tour.UpdateTourRequest;
import com.project.tour.dto.scheduler.SchedulerTourDtos.TourDetailResponse;
import com.project.tour.dto.scheduler.SchedulerTourDtos.TourSummaryResponse;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.service.scheduler.TourService;
import com.project.tour.service.scheduler.SchedulerTourService;

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
        private final SchedulerTourService schedulerTourService;

        public TourController(TourService tourService, SchedulerTourService schedulerTourService) {
                this.tourService = tourService;
                this.schedulerTourService = schedulerTourService;
        }

        // --- SCHEDULER DASHBOARD / TRACKING ENDPOINTS (Gộp từ SchedulerTourController)
        // ---

        @GetMapping("/summary")
        public ResponseEntity<List<TourSummaryResponse>> getActiveTours(
                        @RequestParam(required = false) List<String> statuses) {
                return ResponseEntity.ok(schedulerTourService.getToursByStatuses(statuses));
        }

        @GetMapping("/{tourId}/details")
        public ResponseEntity<TourDetailResponse> getTourDetails(@PathVariable UUID tourId) {
                return ResponseEntity.ok(schedulerTourService.getTourDetailsWithTracking(tourId));
        }
        // --- TOUR BASIC CRUD & WORKFLOW ---

        @PostMapping
        public ResponseEntity<TourResponse> createTour(
                        @Valid @RequestBody CreateTourRequest request) {
                TourResponse response = tourService.createTour(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @PostMapping("/with-route")
        public ResponseEntity<CreateTourWithRouteResponse> createTourWithRoute(
                        @Valid @RequestBody CreateTourWithRouteRequest request) {
                CreateTourWithRouteResponse response = tourService.createTourWithRoute(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @GetMapping("/{id}")
        public ResponseEntity<TourResponse> getTourById(@PathVariable UUID id) {
                return ResponseEntity.ok(tourService.getTourById(id));
        }

        @GetMapping("/code/{code}")
        public ResponseEntity<TourResponse> getTourByCode(@PathVariable String code) {
                return ResponseEntity.ok(tourService.getTourByCode(code));
        }

        @GetMapping
        public ResponseEntity<List<TourResponse>> getTours(
                        @RequestParam(required = false) UUID cruiseId,
                        @RequestParam(required = false) TourStatusTrip statusTrip) {
                return ResponseEntity.ok(tourService.getTours(cruiseId, statusTrip));
        }

        @PatchMapping("/{id}")
        public ResponseEntity<TourResponse> updateTour(
                        @PathVariable UUID id,
                        @Valid @RequestBody UpdateTourRequest request) {
                return ResponseEntity.ok(tourService.updateTour(id, request));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteTour(@PathVariable UUID id) {
                tourService.deleteTour(id);
                return ResponseEntity.noContent().build();
        }

        @PatchMapping("/{id}/submit-for-approval")
        public ResponseEntity<TourResponse> submitForApproval(@PathVariable UUID id) {
                return ResponseEntity.ok(tourService.submitForApproval(id));
        }

}