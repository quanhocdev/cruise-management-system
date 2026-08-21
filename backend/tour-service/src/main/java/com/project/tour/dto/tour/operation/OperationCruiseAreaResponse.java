package com.project.tour.dto.tour.operation;

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