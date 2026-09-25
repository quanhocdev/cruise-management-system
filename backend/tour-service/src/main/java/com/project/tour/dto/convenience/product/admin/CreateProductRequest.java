package com.project.tour.dto.convenience.product.admin;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record CreateProductRequest(

        @NotBlank(message = "Product name is required") String name,

        String description,

        @NotNull(message = "Price is required") @DecimalMin(value = "0.0", inclusive = true, message = "Price must be greater than or equal to 0") BigDecimal price,

        @NotNull(message = "Stock quantity is required") @PositiveOrZero(message = "Stock quantity must be greater than or equal to 0") Integer stockQuantity,

        MultipartFile image) {
}