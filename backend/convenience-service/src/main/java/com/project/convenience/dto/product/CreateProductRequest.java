package com.project.convenience.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record CreateProductRequest(
                @NotBlank(message = "Product name is required") @Size(max = 150, message = "Product name must not exceed 150 characters") String name,

                @Size(max = 5000, message = "Description must not exceed 5000 characters") String description,

                @NotNull(message = "Price is required") @DecimalMin(value = "0.0", inclusive = true, message = "Price must be greater than or equal to 0") BigDecimal price,

                @NotNull(message = "Stock quantity is required") @PositiveOrZero(message = "Stock quantity must be greater than or equal to 0") Integer stockQuantity,

                MultipartFile image) {
}