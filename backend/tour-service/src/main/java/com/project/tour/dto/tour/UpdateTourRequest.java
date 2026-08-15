package com.project.tour.dto.tour;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateTourRequest(

        @NotBlank(message = "Tour code is required") @Size(max = 50, message = "Tour code must not exceed 50 characters") String code,

        @NotBlank(message = "Tour name is required") @Size(max = 200, message = "Tour name must not exceed 200 characters") String name,

        @Size(max = 5000, message = "Description must not exceed 5000 characters") String description,

        @NotNull(message = "Day start is required") @Min(value = 1, message = "Day start must be at least 1") Integer dayStart,

        @NotNull(message = "Day end is required") @Min(value = 1, message = "Day end must be at least 1") Integer dayEnd) {
}