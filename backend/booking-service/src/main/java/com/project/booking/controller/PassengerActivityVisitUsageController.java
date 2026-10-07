package com.project.booking.controller;

import com.project.booking.dto.shore.ActivityVisitUsageResponse;
import com.project.booking.service.shore.ActivityVisitUsageService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/passengers/activity-visit-usages")
public class PassengerActivityVisitUsageController {

    private final ActivityVisitUsageService activityVisitUsageService;

    public PassengerActivityVisitUsageController(
            ActivityVisitUsageService activityVisitUsageService) {

        this.activityVisitUsageService = activityVisitUsageService;
    }

    @GetMapping
    public ResponseEntity<List<ActivityVisitUsageResponse>> getMine(
            Principal principal) {

        Long userId = extractUserId(principal);

        return ResponseEntity.ok(
                activityVisitUsageService.getByUserId(userId));
    }

    private Long extractUserId(Principal principal) {

        if (principal instanceof org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken jwtAuth) {

            Object userIdClaim = jwtAuth.getTokenAttributes()
                    .get("userId");

            if (userIdClaim != null) {
                return Long.valueOf(
                        userIdClaim.toString());
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