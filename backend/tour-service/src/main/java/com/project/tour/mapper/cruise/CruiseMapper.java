package com.project.tour.mapper.cruise;

import com.project.tour.dto.cruise.CreateCruiseRequest;
import com.project.tour.dto.cruise.CruiseResponse;
import com.project.tour.dto.cruise.UpdateCruiseRequest;
import com.project.tour.model.Cruise;

public class CruiseMapper {

    public static Cruise toEntity(CreateCruiseRequest request) {

        Cruise cruise = new Cruise();

        cruise.setName(request.getName());
        cruise.setCode(request.getCode());
        cruise.setDescription(request.getDescription());
        cruise.setMaxPassengers(request.getMaxPassengers());

        return cruise;
    }

    public static void updateEntity(
            Cruise cruise,
            UpdateCruiseRequest request) {

        cruise.setName(request.getName());
        cruise.setCode(request.getCode());
        cruise.setDescription(request.getDescription());
        cruise.setMaxPassengers(request.getMaxPassengers());
    }

    public static CruiseResponse toResponse(Cruise cruise) {

        CruiseResponse response = new CruiseResponse();

        response.setId(cruise.getId());
        response.setName(cruise.getName());
        response.setCode(cruise.getCode());
        response.setDescription(cruise.getDescription());
        response.setMaxPassengers(cruise.getMaxPassengers());
        response.setImageUrl(cruise.getImageUrl());
        response.setImagePublicId(cruise.getImagePublicId());
        response.setStatus(cruise.getStatus());
        response.setCreatedAt(cruise.getCreatedAt());
        response.setUpdatedAt(cruise.getUpdatedAt());

        return response;
    }
}