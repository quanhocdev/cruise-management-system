package com.project.tour.dto.product.area;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateProductAreaRequest {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }
}