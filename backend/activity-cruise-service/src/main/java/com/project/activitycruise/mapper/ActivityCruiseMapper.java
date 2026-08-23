package com.project.activitycruise.mapper;

import com.project.activitycruise.dto.ActivityCruiseResponse;
import com.project.activitycruise.dto.CreateActivityCruiseRequest;
import com.project.activitycruise.dto.UpdateActivityCruiseRequest;
import com.project.activitycruise.model.ActivityCruise;

public final class ActivityCruiseMapper {

    private ActivityCruiseMapper() {
        // Private constructor để ngăn tạo instance của Utility Class
    }

    public static ActivityCruise toEntity(CreateActivityCruiseRequest request) {
        if (request == null) {
            return null;
        }

        ActivityCruise entity = new ActivityCruise();
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setStatus(request.status());
        return entity;
    }

    public static ActivityCruiseResponse toResponse(ActivityCruise entity) {
        if (entity == null) {
            return null;
        }

        return new ActivityCruiseResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getImageUrl(),
                entity.getImagePublicId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static void updateEntity(ActivityCruise entity, UpdateActivityCruiseRequest request) {
        if (entity == null || request == null) {
            return;
        }

        if (request.name() != null && !request.name().isBlank()) {
            entity.setName(request.name());
        }
        if (request.description() != null) {
            entity.setDescription(request.description());
        }
        if (request.status() != null) {
            entity.setStatus(request.status());
        }
    }
}