
package com.project.booking.controller.convenience;

import com.project.booking.dto.convenience.product.ProductUsageManagementResponse;
import com.project.booking.dto.convenience.product.ProductUsageRequest;
import com.project.booking.dto.convenience.product.ProductUsageResponse;
import com.project.booking.service.convenience.product.ProductUsageService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/convenience/product-usages")
public class ProductUsageController {

    private final ProductUsageService productUsageService;

    public ProductUsageController(ProductUsageService productUsageService) {
        this.productUsageService = productUsageService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CONVENIENCE')")
    public ResponseEntity<ProductUsageResponse> create(
            @Valid @RequestBody ProductUsageRequest request,
            Principal principal) {

        UUID actorId = extractUserId(principal);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productUsageService.create(request, actorId));
    }

    @GetMapping
    @PreAuthorize("hasRole('CONVENIENCE')")
    public ResponseEntity<List<ProductUsageManagementResponse>> getManagementUsages() {
        return ResponseEntity.ok(
                productUsageService.getManagementUsages());
    }

    @GetMapping("/booking-passenger/{bookingPassengerId}")
    @PreAuthorize("hasRole('CONVENIENCE')")
    public ResponseEntity<List<ProductUsageResponse>> getByBookingPassenger(
            @PathVariable Long bookingPassengerId) {

        return ResponseEntity.ok(
                productUsageService.getByBookingPassengerId(
                        bookingPassengerId));
    }

    private UUID extractUserId(Principal principal) {

        if (principal instanceof JwtAuthenticationToken jwtAuth) {

            Object userIdClaim = jwtAuth.getTokenAttributes().get("userId");

            if (userIdClaim != null) {
                try {
                    return UUID.fromString(userIdClaim.toString());
                } catch (IllegalArgumentException e) {
                    throw new IllegalStateException(
                            "userId trong token không đúng định dạng UUID.");
                }
            }
        }

        if (principal != null) {
            try {
                return UUID.fromString(principal.getName());
            } catch (Exception e) {
                throw new IllegalStateException(
                        "Không thể xác định userId dạng UUID từ token xác thực.");
            }
        }

        throw new IllegalStateException(
                "Không tìm thấy thông tin tài khoản đăng nhập.");
    }
}
