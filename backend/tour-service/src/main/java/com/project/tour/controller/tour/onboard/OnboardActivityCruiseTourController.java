package com.project.tour.controller.tour.onboard;

import com.project.tour.dto.tour.onboard.OnboardActivityCruiseTourResponse;
import com.project.tour.service.tour.onboard.OnboardActivityCruiseTourService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/onboard/activity-cruise-tours")
public class OnboardActivityCruiseTourController {

    private final OnboardActivityCruiseTourService onboardService;

    public OnboardActivityCruiseTourController(
            OnboardActivityCruiseTourService onboardService) {

        this.onboardService = onboardService;
    }

    /**
     * Lấy các hoạt động mà ONBOARD cần cấu hình.
     *
     * Chỉ lấy:
     * - Tour đã APPROVED
     * - ActivityCruiseTour đang WAITING_CONFIG
     */
    @GetMapping("/pending-config")
    public ResponseEntity<List<OnboardActivityCruiseTourResponse>> getPendingConfig() {

        return ResponseEntity.ok(
                onboardService.getPendingConfig());
    }
}