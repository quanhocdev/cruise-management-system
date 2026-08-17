package com.project.tour.controller.tour.onboard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import com.project.tour.service.tour.onboard.OnboardTourService;
import com.project.tour.dto.tour.TourResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/onboard/tours")
@RestController
public class OnboardTourController {
    private final OnboardTourService onboardTourService;

    public OnboardTourController(
            OnboardTourService onboardTourService) {

        this.onboardTourService = onboardTourService;
    }

    @GetMapping("/approved")
    public ResponseEntity<List<TourResponse>> getApprovedTours() {

        return ResponseEntity.ok(
                onboardTourService.getApprovedTours());
    }
}
