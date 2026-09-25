package com.project.tour.controller.convenience.product;

import com.project.tour.dto.convenience.product.convenience.ProductConvenienceResponse;
import com.project.tour.service.convenience.product.ProductConvenienceService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/convenience/products")
public class ProductConvenienceController {

    private final ProductConvenienceService productConvenienceService;

    public ProductConvenienceController(
            ProductConvenienceService productConvenienceService) {

        this.productConvenienceService = productConvenienceService;
    }

    // =====================================================
    // GET ACTIVE PRODUCTS
    // =====================================================

    @GetMapping
    public ResponseEntity<List<ProductConvenienceResponse>> getProducts() {

        List<ProductConvenienceResponse> response = productConvenienceService.getProducts();

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // GET PRODUCT BY ID
    // =====================================================

    @GetMapping("/{productId}")
    public ResponseEntity<ProductConvenienceResponse> getProductById(
            @PathVariable UUID productId) {

        ProductConvenienceResponse response = productConvenienceService.getProductById(
                productId);

        return ResponseEntity.ok(response);
    }
}