package com.project.tour.dto.tour;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateTourRequest(

        @NotBlank(message = "Tour code is required") @Size(max = 50, message = "Tour code must not exceed 50 characters") String code,

        @NotBlank(message = "Tour name is required") @Size(max = 200, message = "Tour name must not exceed 200 characters") String name,

        @Size(max = 5000, message = "Description must not exceed 5000 characters") String description,

        @NotNull(message = "Start date is required") LocalDate startDate,

        @NotNull(message = "End date is required") LocalDate endDate

) {
}