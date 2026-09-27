package com.project.tour.dto.operation.assignment.cruise;

import com.project.tour.model.enums.cruise.CruiseAreaStatus;

import java.util.UUID;

public record OperationCruiseAreaResponse(

        UUID id,

        String name,

        String description,

        CruiseAreaStatus status,

        String imageUrl

) {
}