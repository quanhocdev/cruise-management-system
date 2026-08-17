package com.project.tour.dto.tour.operation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelTourRequest(

        @NotBlank(message = "Cancel reason is required") @Size(max = 1000, message = "Cancel reason must not exceed 1000 characters") String reason

) {
}