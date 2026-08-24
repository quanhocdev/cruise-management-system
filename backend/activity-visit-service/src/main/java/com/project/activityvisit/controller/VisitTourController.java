package com.project.activityvisit.controller;

import com.project.activityvisit.dto.CreateVisitTourRequest;
import com.project.activityvisit.dto.UpdateVisitTourRequest;
import com.project.activityvisit.dto.VisitTourResponse;
import com.project.activityvisit.service.VisitTourService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shore/visit-tours")
public class VisitTourController {

        private final VisitTourService visitTourService;

        public VisitTourController(
                        VisitTourService visitTourService) {

                this.visitTourService = visitTourService;
        }

        // =====================================================
        // GET ALL
        // =====================================================

        @GetMapping
        public ResponseEntity<List<VisitTourResponse>> getAll() {

                return ResponseEntity.ok(
                                visitTourService.getAll());
        }

        // =====================================================
        // GET BY ID
        // =====================================================

        @GetMapping("/{id}")
        public ResponseEntity<VisitTourResponse> getById(
                        @PathVariable UUID id) {

                return ResponseEntity.ok(
                                visitTourService.getById(id));
        }

        // =====================================================
        // GET BY SCHEDULE STOP
        // =====================================================

        @GetMapping("/schedule-stop/{scheduleStopId}")
        public ResponseEntity<List<VisitTourResponse>> getByScheduleStop(
                        @PathVariable UUID scheduleStopId) {

                return ResponseEntity.ok(
                                visitTourService.getByScheduleStop(
                                                scheduleStopId));
        }

        // =====================================================
        // GET BY TOUR
        // =====================================================

        @GetMapping("/tour/{tourId}")
        public ResponseEntity<List<VisitTourResponse>> getByTour(
                        @PathVariable UUID tourId) {

                return ResponseEntity.ok(
                                visitTourService.getByTour(tourId));
        }

        // =====================================================
        // CREATE
        // =====================================================

        @PostMapping("/schedule-stops/{scheduleStopId}/visit-tours")
        public ResponseEntity<VisitTourResponse> create(
                        @PathVariable UUID scheduleStopId,
                        @RequestBody CreateVisitTourRequest request) {

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(
                                                visitTourService.create(
                                                                scheduleStopId,
                                                                request));
        }

        // =====================================================
        // PATCH
        // =====================================================

        @PatchMapping("/{id}")
        public ResponseEntity<VisitTourResponse> update(
                        @PathVariable UUID id,
                        @RequestBody UpdateVisitTourRequest request) {

                return ResponseEntity.ok(
                                visitTourService.update(
                                                id,
                                                request));
        }

        // =====================================================
        // DELETE
        // =====================================================

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(
                        @PathVariable UUID id) {

                visitTourService.delete(id);

                return ResponseEntity
                                .noContent()
                                .build();
        }
}