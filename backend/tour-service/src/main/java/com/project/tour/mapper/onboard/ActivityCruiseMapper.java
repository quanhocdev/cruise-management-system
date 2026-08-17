package com.project.tour.mapper.onboard;

import com.project.tour.dto.onboard.ActivityCruiseResponse;
import com.project.tour.dto.onboard.CreateActivityCruiseRequest;
import com.project.tour.dto.onboard.UpdateActivityCruiseRequest;
import com.project.tour.model.ActivityCruise;
import org.springframework.stereotype.Component;

@Component
public class ActivityCruiseMapper {

    public ActivityCruise toEntity(CreateActivityCruiseRequest request) {
        if (request == null)
            return null;

        ActivityCruise entity = new ActivityCruise();
        entity.setCruiseAreaId(request.cruiseAreaId());
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setStartTime(request.startTime());
        entity.setEndTime(request.endTime());
        entity.setMaxPassengers(request.maxPassengers());
        entity.setPrice(request.price());
        entity.setStatus(request.status() != null ? request.status() : "ACTIVE");
        entity.setImageUrl(request.imageUrl());
        entity.setImagePublicId(request.imagePublicId());
        return entity;
    }

    public ActivityCruiseResponse toResponse(ActivityCruise entity) {
        if (entity == null)
            return null;

        return new ActivityCruiseResponse(
                entity.getId(),
                entity.getCruiseAreaId(),
                entity.getName(),
                entity.getDescription(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getMaxPassengers(),
                entity.getPrice(),
                entity.getStatus(),
                entity.getImageUrl(),
                entity.getImagePublicId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public void updateEntityFromRequest(UpdateActivityCruiseRequest request, ActivityCruise entity) {
        if (request == null || entity == null)
            return;

        entity.setCruiseAreaId(request.cruiseAreaId());
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setStartTime(request.startTime());
        entity.setEndTime(request.endTime());
        entity.setMaxPassengers(request.maxPassengers());
        entity.setPrice(request.price());
        if (request.status() != null) {
            entity.setStatus(request.status());
        }
        entity.setImageUrl(request.imageUrl());
        entity.setImagePublicId(request.imagePublicId());
    }
}