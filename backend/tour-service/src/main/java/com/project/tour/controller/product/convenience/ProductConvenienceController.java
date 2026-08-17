package com.project.tour.controller.product.convenience;

import com.project.tour.dto.product.ProductConvenienceResponse;
import com.project.tour.service.product.ProductConvenienceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/convenience/products")
public class ProductConvenienceController {

    private final ProductConvenienceService productConvenienceService;

    public ProductConvenienceController(ProductConvenienceService productConvenienceService) {
        this.productConvenienceService = productConvenienceService;
    }

    /*
     * =====================================================
     * GET ALL PRODUCTS FOR CONVENIENCE (READ-ONLY)
     * =====================================================
     */
    @GetMapping
    public ResponseEntity<List<ProductConvenienceResponse>> getProducts() {
        List<ProductConvenienceResponse> response = productConvenienceService.getProducts();
        return ResponseEntity.ok(response);
    }

    /*
     * =====================================================
     * GET PRODUCT BY ID FOR CONVENIENCE (READ-ONLY)
     * =====================================================
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ProductConvenienceResponse> getProductById(@PathVariable UUID productId) {
        ProductConvenienceResponse response = productConvenienceService.getProductById(productId);
        return ResponseEntity.ok(response);
    }
}