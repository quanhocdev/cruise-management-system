package com.project.convenience.dto.product.convenience;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ProductTourConfigRequest(

                @NotNull(message = "Product không được để trống") UUID productId,

                @NotNull(message = "Số lượng không được để trống") @Positive(message = "Số lượng phải lớn hơn 0") Integer quantity

) {
}