package com.project.tour.dto.tour.operation;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ServiceTourAssignmentRequest(

        @NotNull(message = "Tour không được để trống") UUID tourId,

        @NotNull(message = "Khu vực du thuyền không được để trống") UUID cruiseAreaId

) {
}