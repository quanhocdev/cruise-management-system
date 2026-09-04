package com.project.convenience.mapper;

import com.project.convenience.dto.service.admin.CreateServiceRequest;
import com.project.convenience.dto.service.convenience.ServiceConvenienceResponse;
import com.project.convenience.dto.service.admin.ServiceResponse;
import com.project.convenience.dto.service.admin.UpdateServiceRequest;
import com.project.convenience.model.Service;

public class ServiceMapper {

    public static Service toEntity(
            CreateServiceRequest request) {

        Service service = new Service();

        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setDurationMinutes(request.getDurationMinutes());
        service.setMaxPassengers(request.getMaxPassengers());

        return service;
    }

    public static void updateEntity(
            Service service,
            UpdateServiceRequest request) {

        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setDurationMinutes(request.getDurationMinutes());
        service.setMaxPassengers(request.getMaxPassengers());
        service.setStatus(request.getStatus());
    }

    public static ServiceResponse toResponse(
            Service service) {

        ServiceResponse response = new ServiceResponse();

        response.setId(service.getId());
        response.setName(service.getName());
        response.setDescription(service.getDescription());
        response.setPrice(service.getPrice());
        response.setDurationMinutes(service.getDurationMinutes());
        response.setMaxPassengers(service.getMaxPassengers());
        response.setImageUrl(service.getImageUrl());
        response.setImagePublicId(service.getImagePublicId());
        response.setStatus(service.getStatus());
        response.setCreatedAt(service.getCreatedAt());
        response.setUpdatedAt(service.getUpdatedAt());

        return response;
    }

    // CONVENIENCE MAPPER

    public static ServiceConvenienceResponse toConvenienceResponse(Service service) {
        if (service == null) {
            return null;
        }

        return new ServiceConvenienceResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getPrice(),
                service.getDurationMinutes(),
                service.getMaxPassengers(),
                service.getImageUrl());
    }
}