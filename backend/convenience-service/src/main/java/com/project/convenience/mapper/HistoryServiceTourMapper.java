package com.project.convenience.mapper;

import com.project.convenience.dto.service.convenience.HistoryServiceTourResponse;
import com.project.convenience.model.HistoryServiceTour;

public class HistoryServiceTourMapper {

    public static HistoryServiceTourResponse toResponse(
            HistoryServiceTour entity) {

        return new HistoryServiceTourResponse(
                entity.getId(),
                entity.getTourId(),
                entity.getTotalConfigurations(),
                entity.getCompletedAt());
    }
}