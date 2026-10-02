package com.project.booking.controller;

import com.project.booking.dto.ProductUsageResponse;
import com.project.booking.service.ProductUsageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/passengers/product-usages")
public class PassengerProductUsageController {

    private final ProductUsageService productUsageService;

    public PassengerProductUsageController(
            ProductUsageService productUsageService) {
        this.productUsageService = productUsageService;
    }

    @GetMapping
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<List<ProductUsageResponse>> getMine(
            Principal principal) {

        Long userId = extractUserId(principal);

        return ResponseEntity.ok(
                productUsageService.getByUserId(userId));
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
            throw new RuntimeException(
                    "Không thể xác định userId từ token xác thực.");
        }
    }
}