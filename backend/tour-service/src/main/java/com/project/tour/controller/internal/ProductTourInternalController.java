package com.project.tour.controller.internal;

import com.project.tour.dto.internal.ProductUsageInfo;
import com.project.tour.service.internal.ProductTourInternalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/product-tours")
public class ProductTourInternalController {

    private final ProductTourInternalService productTourInternalService;

    public ProductTourInternalController(
            ProductTourInternalService productTourInternalService) {

        this.productTourInternalService = productTourInternalService;
    }

    @GetMapping("/{productTourId}/usage-info")
    public ResponseEntity<ProductUsageInfo> getProductUsageInfo(
            @PathVariable UUID productTourId,
            @RequestParam UUID tourPackageId) {

        return ResponseEntity.ok(
                productTourInternalService.getProductUsageInfo(
                        productTourId,
                        tourPackageId));
    }
}