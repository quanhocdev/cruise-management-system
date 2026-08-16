package com.project.tour.service.service;

import com.project.tour.dto.service.ServiceConvenienceResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.service.ServiceMapper;
import com.project.tour.model.Service;
import com.project.tour.model.enums.ServiceStatus;
import com.project.tour.repository.service.ServiceRepository;

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