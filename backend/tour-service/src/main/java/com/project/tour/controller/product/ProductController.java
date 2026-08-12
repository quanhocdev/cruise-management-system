package com.project.tour.controller.product;

import com.project.tour.dto.product.CreateProductRequest;
import com.project.tour.dto.product.ProductResponse;
import com.project.tour.dto.product.UpdateProductRequest;
import com.project.tour.service.product.ProductService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/areas/{areaId}/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(
            ProductService productService) {

        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @PathVariable UUID areaId,
            @Valid @ModelAttribute CreateProductRequest request) {

        ProductResponse response = productService.createProduct(
                areaId,
                request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts(
            @PathVariable UUID areaId,
            @RequestParam(defaultValue = "false") boolean activeOnly) {

        List<ProductResponse> response;

        if (activeOnly) {
            response = productService.getActiveProductsByArea(
                    areaId);
        } else {
            response = productService.getProductsByArea(
                    areaId);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable UUID areaId,
            @PathVariable UUID productId) {

        return ResponseEntity.ok(
                productService.getProductById(
                        areaId,
                        productId));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID areaId,
            @PathVariable UUID productId,
            @Valid @ModelAttribute UpdateProductRequest request) {

        return ResponseEntity.ok(
                productService.updateProduct(
                        areaId,
                        productId,
                        request));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable UUID areaId,
            @PathVariable UUID productId) {

        productService.deleteProduct(
                areaId,
                productId);

        return ResponseEntity.noContent().build();
    }
}