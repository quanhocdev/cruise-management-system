package com.project.convenience.mapper;

import com.project.convenience.dto.product.convenience.HistoryProductTourResponse;
import com.project.convenience.model.HistoryProductTour;

public class HistoryProductTourMapper {

    public static HistoryProductTourResponse toResponse(
            HistoryProductTour entity) {

        return new HistoryProductTourResponse(
                entity.getId(),
                entity.getTourId(),
                entity.getTotalConfigurations(),
                entity.getCompletedAt());
    }
}