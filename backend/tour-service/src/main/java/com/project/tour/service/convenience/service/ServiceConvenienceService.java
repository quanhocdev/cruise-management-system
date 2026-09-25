package com.project.tour.service.convenience.service;

import com.project.tour.dto.convenience.service.convenience.ServiceConvenienceResponse;
import com.project.tour.mapper.convenience.ServiceMapper;
import com.project.tour.model.convenience.enums.ServiceStatus;
import com.project.tour.model.convenience.service.Service;
import com.project.tour.repository.convenience.ServiceRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
@Transactional(readOnly = true)
public class ServiceConvenienceService {

    private final ServiceRepository serviceRepository;

    public ServiceConvenienceService(
            ServiceRepository serviceRepository) {

        this.serviceRepository = serviceRepository;
    }

    public List<ServiceConvenienceResponse> getActiveServices() {

        return serviceRepository
                .findAllByStatusOrderByNameAsc(
                        ServiceStatus.ACTIVE)
                .stream()
                .map(ServiceMapper::toConvenienceResponse)
                .toList();
    }

    public ServiceConvenienceResponse getServiceById(
            UUID serviceId) {

        Service service = serviceRepository
                .findById(serviceId)
                .orElseThrow(() -> new RuntimeException(
                        "Service not found"));

        return ServiceMapper.toConvenienceResponse(service);
    }
}