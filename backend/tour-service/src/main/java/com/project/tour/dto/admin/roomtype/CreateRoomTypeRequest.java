package com.project.tour.dto.admin.roomtype;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record CreateRoomTypeRequest(
        @NotBlank(message = "Room type name is required") @Size(max = 100, message = "Room type name must not exceed 100 characters") String name,

        @Size(max = 5000, message = "Description must not exceed 5000 characters") String description,

        @DecimalMin(value = "0.0", message = "Price must not be negative") @Digits(integer = 17, fraction = 2) BigDecimal price,

        @Min(value = 1, message = "Capacity must be at least 1") Integer capacity,

        MultipartFile image) {
}