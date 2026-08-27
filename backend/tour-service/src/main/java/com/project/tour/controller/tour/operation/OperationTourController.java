package com.project.tour.controller.tour.operation;

import com.project.tour.dto.cruise.CruiseAvailabilityResponse;
import com.project.tour.dto.tour.TourResponse;
import com.project.tour.dto.tour.operation.OperationCruiseLayoutResponse;
import com.project.tour.service.tour.operation.OperationTourService;
import com.project.tour.service.tour.operation.TourCruiseAssignmentService;
import com.project.tour.service.tour.operation.TourLayoutService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/operation/tours")
public class OperationTourController {

        private final OperationTourService operationTourService;
        private final TourCruiseAssignmentService tourCruiseAssignmentService;
        private final TourLayoutService tourLayoutService;

        public OperationTourController(
                        OperationTourService operationTourService,
                        TourCruiseAssignmentService tourCruiseAssignmentService,
                        TourLayoutService tourLayoutService) {

                this.operationTourService = operationTourService;
                this.tourCruiseAssignmentService = tourCruiseAssignmentService;
                this.tourLayoutService = tourLayoutService;
        }

        // =====================================================
        // GET TOURS WAITING FOR APPROVAL
        // =====================================================
        @GetMapping("/pending")
        public ResponseEntity<List<TourResponse>> getPendingTours() {
                return ResponseEntity.ok(operationTourService.getPendingTours());
        }

        // =====================================================
        // GET AVAILABLE CRUISES
        // =====================================================
        @GetMapping("/{id}/available-cruises")
        public ResponseEntity<List<CruiseAvailabilityResponse>> getAvailableCruises(
                        @PathVariable UUID id) {
                return ResponseEntity.ok(tourCruiseAssignmentService.getAvailableCruises(id));
        }

        // =====================================================
        // GET CRUISE LAYOUT
        // =====================================================
        @GetMapping("/{id}/cruise-layout")
        public ResponseEntity<List<OperationCruiseLayoutResponse>> getCruiseLayout(
                        @PathVariable UUID id) {
                return ResponseEntity.ok(tourLayoutService.getCruiseLayout(id));
        }

        // =====================================================
        // 1. GÁN DU THUYỀN CHO TOUR (Vẫn giữ trạng thái PENDING)
        // =====================================================
        @PostMapping("/{id}/assign-cruise")
        public ResponseEntity<TourResponse> assignCruise(
                        @PathVariable UUID id,
                        @RequestParam UUID cruiseId) {
                return ResponseEntity.ok(tourCruiseAssignmentService.assignCruise(id, cruiseId));
        }

        // =====================================================
        // 2. DUYỆT TOUR (Chỉ gọi sau khi đã gán du thuyền + phân công xong)
        // =====================================================
        @PostMapping("/{id}/approve")
        public ResponseEntity<TourResponse> approveTour(
                        @PathVariable UUID id) {

                return ResponseEntity.ok(
                                operationTourService.approveTour(id));
        }

        // =====================================================
        // GET APPROVED TOURS
        // =====================================================
        @GetMapping("/approved")
        public ResponseEntity<List<TourResponse>> getApprovedTours() {
                return ResponseEntity.ok(operationTourService.getApprovedTours());
        }
}