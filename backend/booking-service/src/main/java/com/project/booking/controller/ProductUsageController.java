package com.project.booking.controller;

import com.project.booking.dto.convenience.product.ProductUsageRequest;
import com.project.booking.dto.convenience.product.ProductUsageResponse;
import com.project.booking.service.ProductUsageService;
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

    public ProductUsageController(
            ProductUsageService productUsageService) {
        this.productUsageService = productUsageService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CONVENIENCE')")
    public ResponseEntity<ProductUsageResponse> create(
            @Valid @RequestBody ProductUsageRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productUsageService.create(request));
    }

    @GetMapping("/booking-passenger/{bookingPassengerId}")
    @PreAuthorize("hasRole('CONVENIENCE')")
    public ResponseEntity<List<ProductUsageResponse>> getByBookingPassenger(
            @PathVariable Long bookingPassengerId) {

        return ResponseEntity.ok(
                productUsageService
                        .getByBookingPassengerId(bookingPassengerId));
    }
}