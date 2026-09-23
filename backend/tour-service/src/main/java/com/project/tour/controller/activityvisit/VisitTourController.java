package com.project.tour.controller.activityvisit;

import com.project.tour.dto.activityvisit.CreateVisitTourRequest;
import com.project.tour.dto.activityvisit.TourVisitSyncResponse;
import com.project.tour.dto.activityvisit.UpdateVisitTourRequest;
import com.project.tour.dto.activityvisit.VisitTourResponse;
import com.project.tour.service.activityvisit.VisitTourMasterService;
import com.project.tour.service.activityvisit.VisitTourService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shore/visit-tours")
public class VisitTourController {

        private final VisitTourService visitTourService;
        private final VisitTourMasterService visitTourMasterService;

        public VisitTourController(
                        VisitTourService visitTourService,
                        VisitTourMasterService visitTourMasterService) {

                this.visitTourService = visitTourService;
                this.visitTourMasterService = visitTourMasterService;
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
                                visitTourService.getByScheduleStop(scheduleStopId));
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

        // =====================================================
        // GET MASTER TOUR
        // =====================================================

        @GetMapping("/master/{tourId}")
        public ResponseEntity<TourVisitSyncResponse> getMasterTourById(
                        @PathVariable UUID tourId) {

                return ResponseEntity.ok(
                                visitTourMasterService.getMasterTourById(tourId));
        }

        // =====================================================
        // GET ALL MASTER TOURS
        // =====================================================

        @GetMapping("/masters")
        public ResponseEntity<List<TourVisitSyncResponse>> getAllMasterTours() {

                return ResponseEntity.ok(
                                visitTourMasterService.getAllMasterTours());
        }
}