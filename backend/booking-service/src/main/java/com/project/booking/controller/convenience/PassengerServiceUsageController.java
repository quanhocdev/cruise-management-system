package com.project.booking.controller.convenience;

import java.util.UUID;

import com.project.booking.dto.convenience.service.ServiceUsageResponse;
import com.project.booking.service.convenience.service.ServiceUsageService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@RestController
@RequestMapping("/api/passengers/service-usages")
public class PassengerServiceUsageController {

    private final ServiceUsageService serviceUsageService;

    public PassengerServiceUsageController(
            ServiceUsageService serviceUsageService) {
        this.serviceUsageService = serviceUsageService;
    }

    @GetMapping
    public ResponseEntity<List<ServiceUsageResponse>> getMine(
            Principal principal) {

        UUID userId = extractUserId(principal);

        return ResponseEntity.ok(
                serviceUsageService.getByUserId(userId));
    }

    private UUID extractUserId(Principal principal) {

        if (principal instanceof JwtAuthenticationToken jwtAuth) {

            Object userIdClaim = jwtAuth.getTokenAttributes().get("userId");

            if (userIdClaim != null) {
                return UUID.fromString(userIdClaim.toString());
            }
        }

        try {
            return UUID.fromString(principal.getName());
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Không thể xác định userId từ token xác thực.");
        }
    }
}