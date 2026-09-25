package com.project.tour.service.convenience.service;

import com.project.common.dto.UploadResult;
import com.project.common.service.file.FileStorageService;
import com.project.tour.dto.convenience.service.admin.CreateServiceRequest;
import com.project.tour.dto.convenience.service.admin.ServiceResponse;
import com.project.tour.dto.convenience.service.admin.UpdateServiceRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.convenience.ServiceMapper;
import com.project.tour.model.convenience.enums.ServiceStatus;
import com.project.tour.model.convenience.service.Service;
import com.project.tour.repository.convenience.ServiceRepository;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
@Transactional
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final FileStorageService fileStorageService;

    public ServiceService(
            ServiceRepository serviceRepository,
            FileStorageService fileStorageService) {

        this.serviceRepository = serviceRepository;
        this.fileStorageService = fileStorageService;
    }

    public ServiceResponse createService(
            CreateServiceRequest request) {

        if (serviceRepository.existsByNameIgnoreCase(
                request.getName())) {

            throw new AppException(
                    "Service name already exists",
                    HttpStatus.CONFLICT);
        }

        Service service = ServiceMapper.toEntity(request);

        if (request.getImage() != null
                && !request.getImage().isEmpty()) {

            UploadResult uploadResult = fileStorageService.saveMultipart(
                    request.getImage(),
                    "services");

            service.setImageUrl(
                    uploadResult.getUrl());

            service.setImagePublicId(
                    uploadResult.getPublicId());
        }

        Service saved = serviceRepository.save(service);

        return ServiceMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ServiceResponse getServiceById(
            UUID serviceId) {

        return ServiceMapper.toResponse(
                findById(serviceId));
    }

    @Transactional(readOnly = true)
    public List<ServiceResponse> getServices(
            boolean activeOnly) {

        List<Service> services;

        if (activeOnly) {

            services = serviceRepository
                    .findAllByStatusOrderByNameAsc(
                            ServiceStatus.ACTIVE);

        } else {

            services = serviceRepository
                    .findAllByOrderByNameAsc();
        }

        return services.stream()
                .map(ServiceMapper::toResponse)
                .toList();
    }

    public ServiceResponse updateService(
            UUID serviceId,
            UpdateServiceRequest request) {

        Service service = findById(serviceId);

        if (serviceRepository
                .existsByNameIgnoreCaseAndIdNot(
                        request.getName(),
                        serviceId)) {

            throw new AppException(
                    "Service name already exists",
                    HttpStatus.CONFLICT);
        }

        String oldPublicId = service.getImagePublicId();

        ServiceMapper.updateEntity(
                service,
                request);

        if (request.getImage() != null
                && !request.getImage().isEmpty()) {

            UploadResult uploadResult = fileStorageService.saveMultipart(
                    request.getImage(),
                    "services");

            service.setImageUrl(
                    uploadResult.getUrl());

            service.setImagePublicId(
                    uploadResult.getPublicId());

            if (oldPublicId != null
                    && !oldPublicId.isBlank()) {

                fileStorageService.delete(
                        oldPublicId);
            }
        }

        Service updated = serviceRepository.save(service);

        return ServiceMapper.toResponse(updated);
    }

    public void deleteService(
            UUID serviceId) {

        Service service = findById(serviceId);

        if (service.getImagePublicId() != null
                && !service.getImagePublicId().isBlank()) {

            fileStorageService.delete(
                    service.getImagePublicId());
        }

        serviceRepository.delete(service);
    }

    private Service findById(
            UUID serviceId) {

        return serviceRepository
                .findById(serviceId)
                .orElseThrow(() -> new AppException(
                        "Service not found",
                        HttpStatus.NOT_FOUND));
    }
}