package com.project.convenience.controller.product.convenience;

import com.project.convenience.service.product.ProductTourConfigurationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/convenience/product-tours")
public class ProductTourConfigurationController {

    private final ProductTourConfigurationService configurationService;

    public ProductTourConfigurationController(
            ProductTourConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @PostMapping("/{tourId}/complete-configuration")
    public ResponseEntity<Void> completeConfiguration(
            @PathVariable UUID tourId) {

        configurationService.completeConfiguration(tourId);

        return ResponseEntity.ok().build();
    }
}