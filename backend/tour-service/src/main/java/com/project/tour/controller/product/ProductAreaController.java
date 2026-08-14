package com.project.tour.controller.product;

import com.project.tour.dto.product.area.CreateProductAreaRequest;
import com.project.tour.dto.product.area.ProductAreaResponse;
import com.project.tour.service.product.ProductAreaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/convenience/areas/{areaId}/products")
public class ProductAreaController {

    private final ProductAreaService productAreaService;

    public ProductAreaController(
            ProductAreaService productAreaService) {

        this.productAreaService = productAreaService;
    }

    /*
     * =====================================================
     * GET PRODUCTS ASSIGNED TO AREA
     * =====================================================
     */
    @GetMapping
    public ResponseEntity<List<ProductAreaResponse>> getProductsByArea(
            @PathVariable UUID areaId) {

        return ResponseEntity.ok(
                productAreaService.getProductsByArea(areaId));
    }

    /*
     * =====================================================
     * ASSIGN PRODUCT TO AREA
     * =====================================================
     */
    @PostMapping
    public ResponseEntity<ProductAreaResponse> assignProduct(
            @PathVariable UUID areaId,
            @Valid @RequestBody CreateProductAreaRequest request) {

        ProductAreaResponse response = productAreaService.assignProduct(
                areaId,
                request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /*
     * =====================================================
     * REMOVE PRODUCT FROM AREA
     * =====================================================
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeProduct(
            @PathVariable UUID areaId,
            @PathVariable UUID productId) {

        productAreaService.removeProduct(
                areaId,
                productId);

        return ResponseEntity
                .noContent()
                .build();
    }
}