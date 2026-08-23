package com.project.convenience.service.service;

import com.project.convenience.mapper.ServiceMapper;
import com.project.convenience.model.Service;
import com.project.convenience.dto.service.convenience.ServiceConvenienceResponse;
import com.project.convenience.exception.AppException;
import com.project.convenience.model.enums.ServiceStatus;
import com.project.convenience.repository.ServiceRepository;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
@Transactional(readOnly = true)
public class ServiceConvenienceService {

    private final ServiceRepository serviceRepository;

    public ServiceConvenienceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    /*
     * GET ALL ACTIVE SERVICES FOR CONVENIENCE
     */
    public List<ServiceConvenienceResponse> getActiveServices() {
        return serviceRepository
                .findAllByStatusOrderByNameAsc(ServiceStatus.ACTIVE)
                .stream()
                .map(ServiceMapper::toConvenienceResponse)
                .toList();
    }

    /*
     * GET SERVICE BY ID FOR CONVENIENCE
     */
    public ServiceConvenienceResponse getServiceById(UUID serviceId) {
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new AppException(
                        "Service not found",
                        HttpStatus.NOT_FOUND));

        return ServiceMapper.toConvenienceResponse(service);
    }
}