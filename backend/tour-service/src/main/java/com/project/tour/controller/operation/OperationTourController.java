
package com.project.tour.controller.operation;

import com.project.tour.dto.admin.cruise.CruiseAvailabilityResponse;
import com.project.tour.dto.operation.assignment.OperationCruiseLayoutResponse;
import com.project.tour.dto.operation.patrol.PatrolRequirementRequest;
import com.project.tour.dto.operation.patrol.PatrolRequirementResponse;
import com.project.tour.dto.scheduler.tour.TourResponse;
import com.project.tour.exception.AppException;
import com.project.tour.service.operation.ApprovalTourService;
import com.project.tour.service.operation.assignment.TourCruiseAssignmentService;
import com.project.tour.service.operation.assignment.TourLayoutService;
import com.project.tour.service.operation.patrol.OperationRequirePatrolService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/operation/tours")
public class OperationTourController {

        private final ApprovalTourService operationTourService;
        private final TourCruiseAssignmentService tourCruiseAssignmentService;
        private final TourLayoutService tourLayoutService;
        private final OperationRequirePatrolService patrolService;

        public OperationTourController(
                        ApprovalTourService operationTourService,
                        TourCruiseAssignmentService tourCruiseAssignmentService,
                        TourLayoutService tourLayoutService,
                        OperationRequirePatrolService patrolService) {

                this.operationTourService = operationTourService;
                this.tourCruiseAssignmentService = tourCruiseAssignmentService;
                this.tourLayoutService = tourLayoutService;
                this.patrolService = patrolService;
        }

        // GET TOURS WAITING FOR APPROVAL
        @GetMapping("/pending")
        public ResponseEntity<List<TourResponse>> getPendingTours() {
                return ResponseEntity.ok(operationTourService.getPendingTours());
        }

        // GET AVAILABLE CRUISES
        @GetMapping("/{id}/available-cruises")
        public ResponseEntity<List<CruiseAvailabilityResponse>> getAvailableCruises(
                        @PathVariable UUID id) {
                return ResponseEntity.ok(
                                tourCruiseAssignmentService.getAvailableCruises(id));
        }

        // GET CRUISE LAYOUT
        @GetMapping("/{id}/cruise-layout")
        public ResponseEntity<List<OperationCruiseLayoutResponse>> getCruiseLayout(
                        @PathVariable UUID id) {
                return ResponseEntity.ok(tourLayoutService.getCruiseLayout(id));
        }

        // ASSIGN CRUISE TO TOUR
        @PostMapping("/{id}/assign-cruise")
        public ResponseEntity<TourResponse> assignCruise(
                        @PathVariable UUID id,
                        @RequestParam UUID cruiseId) {
                return ResponseEntity.ok(
                                tourCruiseAssignmentService.assignCruise(id, cruiseId));
        }

        // APPROVE TOUR
        @PostMapping("/{id}/approve")
        public ResponseEntity<TourResponse> approveTour(
                        @PathVariable UUID id) {
                return ResponseEntity.ok(operationTourService.approveTour(id));
        }

        // GET APPROVED TOURS
        @GetMapping("/approved")
        public ResponseEntity<List<TourResponse>> getApprovedTours() {
                return ResponseEntity.ok(operationTourService.getApprovedTours());
        }

        // GET READY TOURS
        @GetMapping("/ready")
        public ResponseEntity<List<TourResponse>> getReadyTours() {
                return ResponseEntity.ok(operationTourService.getReadyTours());
        }

        // ==========================================
        // PATROL REQUIREMENTS
        // ==========================================

        /**
         * Lấy danh sách tầng ACTIVE và số lượng nhân viên
         * tuần tra được yêu cầu cho từng tầng.
         */
        @GetMapping("/{tourId}/patrol-requirements")
        public ResponseEntity<List<PatrolRequirementResponse>> getPatrolRequirements(
                        @PathVariable UUID tourId) {

                return ResponseEntity.ok(
                                patrolService.getRequirements(tourId));
        }

        /**
         * Lưu/cập nhật yêu cầu số lượng nhân viên tuần tra
         * theo từng tầng của Tour.
         */
        @PutMapping("/{tourId}/patrol-requirements")
        public ResponseEntity<List<PatrolRequirementResponse>> savePatrolRequirements(
                        @PathVariable UUID tourId,
                        @Valid @RequestBody PatrolRequirementRequest request,
                        @AuthenticationPrincipal Jwt jwt) {

                UUID createdBy = extractUserId(jwt);

                return ResponseEntity.ok(
                                patrolService.saveRequirements(
                                                tourId,
                                                request,
                                                createdBy));
        }

        private UUID extractUserId(Jwt jwt) {

                if (jwt == null) {
                        throw new AppException(
                                        "Không tìm thấy thông tin xác thực",
                                        HttpStatus.UNAUTHORIZED);
                }

                String userId = jwt.getClaimAsString("userId");

                if (userId == null || userId.isBlank()) {
                        throw new AppException(
                                        "JWT không chứa userId",
                                        HttpStatus.UNAUTHORIZED);
                }

                try {
                        return UUID.fromString(userId);
                } catch (IllegalArgumentException exception) {
                        throw new AppException(
                                        "userId trong JWT không hợp lệ",
                                        HttpStatus.UNAUTHORIZED);
                }
        }
}
