package com.project.tour.mapper.admin.cruise;

import com.project.tour.dto.admin.cruise.area.CreateCruiseAreaRequest;
import com.project.tour.dto.admin.cruise.area.CruiseAreaResponse;
import com.project.tour.dto.admin.cruise.area.UpdateCruiseAreaRequest;
import com.project.tour.model.admin.CruiseArea;

public class CruiseAreaMapper {

    public static CruiseArea toEntity(
            CreateCruiseAreaRequest request) {

        CruiseArea area = new CruiseArea();

        area.setName(request.getName());
        area.setDescription(request.getDescription());

        return area;
    }

    public static void updateEntity(
            CruiseArea area,
            UpdateCruiseAreaRequest request) {

        area.setName(request.getName());
        area.setDescription(request.getDescription());
        area.setStatus(request.getStatus());
    }

    public static CruiseAreaResponse toResponse(
            CruiseArea area) {

        CruiseAreaResponse response = new CruiseAreaResponse();

        response.setId(area.getId());

        if (area.getCruiseDeck() != null) {
            response.setCruiseDeckId(
                    area.getCruiseDeck().getId());
        }

        response.setName(area.getName());
        response.setDescription(area.getDescription());
        response.setStatus(area.getStatus());

        return response;
    }
}