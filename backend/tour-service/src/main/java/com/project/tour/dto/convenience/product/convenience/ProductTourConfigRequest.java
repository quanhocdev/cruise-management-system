package com.project.tour.dto.convenience.product.convenience;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ProductTourConfigRequest(

        @NotNull(message = "Product ID is required") UUID productId,

        @NotNull(message = "Quantity is required") @Positive(message = "Quantity must be greater than 0") Integer quantity) {
}