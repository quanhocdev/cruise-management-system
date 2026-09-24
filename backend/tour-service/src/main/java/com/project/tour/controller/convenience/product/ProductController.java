package com.project.tour.controller.convenience.product;

import com.project.tour.dto.convenience.product.admin.CreateProductRequest;
import com.project.tour.dto.convenience.product.admin.ProductResponse;
import com.project.tour.dto.convenience.product.admin.UpdateProductRequest;
import com.project.tour.service.convenience.product.ProductService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(
            ProductService productService) {

        this.productService = productService;
    }

    // =====================================================
    // CREATE
    // =====================================================

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @ModelAttribute CreateProductRequest request) {

        ProductResponse response = productService.createProduct(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =====================================================
    // GET ALL
    // =====================================================

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts(
            @RequestParam(defaultValue = "false") boolean activeOnly) {

        List<ProductResponse> response;

        if (activeOnly) {
            response = productService.getActiveProducts();
        } else {
            response = productService.getProducts();
        }

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // GET BY ID
    // =====================================================

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable UUID productId) {

        return ResponseEntity.ok(
                productService.getProductById(productId));
    }

    // =====================================================
    // UPDATE
    // =====================================================

    @PatchMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID productId,
            @Valid @ModelAttribute UpdateProductRequest request) {

        return ResponseEntity.ok(
                productService.updateProduct(
                        productId,
                        request));
    }

    // =====================================================
    // DELETE
    // =====================================================

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable UUID productId) {

        productService.deleteProduct(productId);

        return ResponseEntity
                .noContent()
                .build();
    }
}