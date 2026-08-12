package com.project.tour.dto.cruise.area;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCruiseAreaRequest(
                @NotBlank(message = "Cruise area name is required") @Size(max = 150, message = "Cruise area name must not exceed 150 characters") String name,
                @Size(max = 5000, message = "Description must not exceed 5000 characters") String description) {
}
