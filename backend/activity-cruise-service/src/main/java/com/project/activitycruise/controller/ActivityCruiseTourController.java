package com.project.activitycruise.controller;

import com.project.activitycruise.dto.ActivityCruiseTourConfigRequest;
import com.project.activitycruise.dto.OnboardActivityCruiseTourResponse;
import com.project.activitycruise.service.ActivityCruiseTourConfigService;
import com.project.activitycruise.service.ActivityCruiseTourService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/onboard/activity-cruise-tours")
public class ActivityCruiseTourController {

        private final ActivityCruiseTourService activityCruiseTourService;
        private final ActivityCruiseTourConfigService configService;

        public ActivityCruiseTourController(
                        ActivityCruiseTourService activityCruiseTourService,
                        ActivityCruiseTourConfigService configService) {

                this.activityCruiseTourService = activityCruiseTourService;
                this.configService = configService;
        }

        // =====================================================
        // GET PENDING CONFIG
        // =====================================================

        /**
         * Lấy các hoạt động mà ONBOARD cần cấu hình.
         *
         * Chỉ lấy:
         * - ActivityCruiseTour đang WAITING_CONFIG
         */
        @GetMapping("/pending-config")
        public ResponseEntity<List<OnboardActivityCruiseTourResponse>> getPendingConfig() {

                return ResponseEntity.ok(
                                activityCruiseTourService.getPendingConfig());
        }

        // =====================================================
        // POST CONFIG
        // =====================================================

        /**
         * ONBOARD cấu hình hoạt động lần đầu.
         *
         * Chỉ được gọi khi:
         * ActivityCruiseTour.status = WAITING_CONFIG
         *
         * Sau khi thành công:
         * WAITING_CONFIG -> NOT_STARTED
         */
        @PostMapping("/{assignmentId}/config")
        public ResponseEntity<OnboardActivityCruiseTourResponse> configure(
                        @PathVariable UUID assignmentId,
                        @Valid @RequestBody ActivityCruiseTourConfigRequest request) {

                return ResponseEntity.ok(
                                configService.configure(
                                                assignmentId,
                                                request));
        }

        // =====================================================
        // PATCH CONFIG
        // =====================================================

        /**
         * ONBOARD cập nhật lại cấu hình hoạt động.
         *
         * Chỉ được cập nhật khi:
         * ActivityCruiseTour.status = NOT_STARTED
         */
        @PatchMapping("/{assignmentId}/config")
        public ResponseEntity<OnboardActivityCruiseTourResponse> updateConfig(
                        @PathVariable UUID assignmentId,
                        @Valid @RequestBody ActivityCruiseTourConfigRequest request) {

                return ResponseEntity.ok(
                                configService.updateConfig(
                                                assignmentId,
                                                request));
        }
}