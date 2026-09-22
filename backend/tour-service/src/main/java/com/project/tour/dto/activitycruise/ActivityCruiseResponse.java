package com.project.tour.dto.activitycruise;

import java.time.LocalDateTime;
import java.util.UUID;

import com.project.tour.model.activitycruise.enums.ActivityCruiseStatus;

public record ActivityCruiseResponse(

        UUID id,

        String name,

        String description,

        ActivityCruiseStatus status,

        String imageUrl,

        String imagePublicId,

        LocalDateTime createdAt,

        LocalDateTime updatedAt) {

}
