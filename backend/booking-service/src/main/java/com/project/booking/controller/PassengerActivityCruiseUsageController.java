package com.project.booking.controller;

import com.project.booking.dto.ActivityCruiseUsageResponse;
import com.project.booking.service.onboard.ActivityCruiseUsageService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/passengers/activity-cruise-usages")
public class PassengerActivityCruiseUsageController {

    private final ActivityCruiseUsageService activityCruiseUsageService;

    public PassengerActivityCruiseUsageController(
            ActivityCruiseUsageService activityCruiseUsageService) {

        this.activityCruiseUsageService = activityCruiseUsageService;
    }

    @GetMapping
    public ResponseEntity<List<ActivityCruiseUsageResponse>> getMine(
            Principal principal) {

        Long userId = extractUserId(principal);

        return ResponseEntity.ok(
                activityCruiseUsageService.getByUserId(userId));
    }

    private Long extractUserId(Principal principal) {

        if (principal instanceof JwtAuthenticationToken jwtAuth) {

            Object userIdClaim = jwtAuth.getTokenAttributes().get("userId");

            if (userIdClaim != null) {
                return Long.valueOf(userIdClaim.toString());
            }
        }

        try {
            return Long.valueOf(principal.getName());

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Không thể xác định userId từ token xác thực.");
        }
    }
}