package com.project.tour.dto.tour.operation;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ApproveTourRequest(

        @NotNull(message = "Cruise id is required") UUID cruiseId

) {
}