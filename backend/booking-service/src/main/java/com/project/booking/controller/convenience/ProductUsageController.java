package com.project.booking.controller.convenience;

import com.project.booking.dto.convenience.product.ProductUsageManagementResponse;
import com.project.booking.dto.convenience.product.ProductUsageRequest;
import com.project.booking.dto.convenience.product.ProductUsageResponse;
import com.project.booking.service.convenience.product.ProductUsageService;

import com.project.booking.service.command.IdempotentCommandService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/convenience/product-usages")
public class ProductUsageController {

    private final ProductUsageService productUsageService;
    private final IdempotentCommandService commands;

    public ProductUsageController(ProductUsageService productUsageService, IdempotentCommandService commands) {
        this.productUsageService = productUsageService;
        this.commands = commands;
    }

    @PostMapping
    @PreAuthorize("hasRole('CONVENIENCE')")
    public ResponseEntity<ProductUsageResponse> create(
            @Valid @RequestBody ProductUsageRequest request) {

        return commands.execute("PRODUCT_USAGE", request.clientRequestId(), request, ProductUsageResponse.class, () -> productUsageService.create(request));
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
}
