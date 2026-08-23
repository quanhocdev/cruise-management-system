package com.project.convenience.controller.product.convenience;

import com.project.convenience.dto.product.convenience.ProductConvenienceResponse;
import com.project.convenience.service.product.ProductConvenienceService;
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

    // Lấy danh sách sản phẩm cho tiện ích (chỉ đọc)
    @GetMapping
    public ResponseEntity<List<ProductConvenienceResponse>> getProducts() {
        List<ProductConvenienceResponse> response = productConvenienceService.getProducts();
        return ResponseEntity.ok(response);
    }

    // Lấy sản phẩm theo ID cho tiện ích (chỉ đọc)
    @GetMapping("/{productId}")
    public ResponseEntity<ProductConvenienceResponse> getProductById(@PathVariable UUID productId) {
        ProductConvenienceResponse response = productConvenienceService.getProductById(productId);
        return ResponseEntity.ok(response);
    }
}