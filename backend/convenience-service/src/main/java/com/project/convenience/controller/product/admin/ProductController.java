package com.project.convenience.controller.product.admin;

import com.project.convenience.dto.product.CreateProductRequest;
import com.project.convenience.dto.product.ProductResponse;
import com.project.convenience.dto.product.UpdateProductRequest;
import com.project.convenience.service.product.ProductService;

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

        /*
         * =====================================================
         * CREATE PRODUCT
         * =====================================================
         */
        @PostMapping
        public ResponseEntity<ProductResponse> createProduct(
                        @Valid @ModelAttribute CreateProductRequest request) {

                ProductResponse response = productService.createProduct(request);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(response);
        }

        /*
         * =====================================================
         * GET ALL PRODUCTS
         * =====================================================
         */
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

        /*
         * =====================================================
         * GET PRODUCT BY ID
         * =====================================================
         */
        @GetMapping("/{productId}")
        public ResponseEntity<ProductResponse> getProductById(
                        @PathVariable UUID productId) {

                return ResponseEntity.ok(
                                productService.getProductById(productId));
        }

        /*
         * =====================================================
         * UPDATE PRODUCT
         * =====================================================
         */
        @PatchMapping("/{productId}")
        public ResponseEntity<ProductResponse> updateProduct(
                        @PathVariable UUID productId,
                        @Valid @ModelAttribute UpdateProductRequest request) {

                return ResponseEntity.ok(
                                productService.updateProduct(
                                                productId,
                                                request));
        }

        /*
         * =====================================================
         * DELETE PRODUCT
         * =====================================================
         */
        @DeleteMapping("/{productId}")
        public ResponseEntity<Void> deleteProduct(
                        @PathVariable UUID productId) {

                productService.deleteProduct(productId);

                return ResponseEntity
                                .noContent()
                                .build();
        }
}