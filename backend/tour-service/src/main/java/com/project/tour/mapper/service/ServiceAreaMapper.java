package com.project.tour.mapper.service;

import com.project.tour.dto.service.area.ServiceAreaResponse;
import com.project.tour.model.ServiceArea;

public class ServiceAreaMapper {

    private ServiceAreaMapper() {
    }

    public static ServiceAreaResponse toResponse(
            ServiceArea serviceArea) {

        ServiceAreaResponse response = new ServiceAreaResponse();

        response.setId(serviceArea.getId());

        response.setAreaId(
                serviceArea.getCruiseArea().getId());

        response.setAreaName(
                serviceArea.getCruiseArea().getName());

        response.setServiceId(
                serviceArea.getService().getId());

        response.setServiceName(
                serviceArea.getService().getName());

        response.setServiceDescription(
                serviceArea.getService().getDescription());

        response.setServicePrice(
                serviceArea.getService().getPrice());

        response.setDurationMinutes(
                serviceArea.getService().getDurationMinutes());

        response.setMaxPassengers(
                serviceArea.getService().getMaxPassengers());

        response.setImageUrl(
                serviceArea.getService().getImageUrl());

        response.setServiceStatus(
                serviceArea.getService().getStatus());

        response.setCreatedAt(
                serviceArea.getCreatedAt());

        response.setUpdatedAt(
                serviceArea.getUpdatedAt());

        return response;
    }
}