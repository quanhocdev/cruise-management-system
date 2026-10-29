
package com.project.tour.dto.scheduler;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTourWithRouteRequest(

        @NotBlank @Size(max = 50) String code,

        @NotBlank @Size(max = 200) String name,

        @Size(max = 5000) String description,

        @NotNull LocalDate startDate,

        @NotNull LocalDate endDate,

        @NotNull UUID departurePortId,

        @NotNull UUID destinationPortId

) {
}
