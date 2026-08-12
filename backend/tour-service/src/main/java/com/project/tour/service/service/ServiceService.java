package com.project.tour.service.service;

import com.project.common.dto.UploadResult;
import com.project.common.service.file.FileStorageService;
import com.project.tour.dto.service.CreateServiceRequest;
import com.project.tour.dto.service.ServiceResponse;
import com.project.tour.dto.service.UpdateServiceRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.service.ServiceMapper;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.Service;
import com.project.tour.model.enums.ServiceStatus;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.repository.service.ServiceRepository;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
@Transactional
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final CruiseAreaRepository cruiseAreaRepository;
    private final FileStorageService fileStorageService;

    public ServiceService(
            ServiceRepository serviceRepository,
            CruiseAreaRepository cruiseAreaRepository,
            FileStorageService fileStorageService) {

        this.serviceRepository = serviceRepository;
        this.cruiseAreaRepository = cruiseAreaRepository;
        this.fileStorageService = fileStorageService;
    }

    public ServiceResponse createService(
            UUID areaId,
            CreateServiceRequest request) {

        CruiseArea area = findArea(areaId);

        if (serviceRepository
                .existsByCruiseArea_IdAndNameIgnoreCase(
                        areaId,
                        request.getName())) {

            throw new AppException(
                    "Service name already exists in this area",
                    HttpStatus.CONFLICT);
        }

        Service service = ServiceMapper.toEntity(request);

        service.setCruiseArea(area);

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
            UUID areaId,
            UUID serviceId) {

        return ServiceMapper.toResponse(
                findById(areaId, serviceId));
    }

    @Transactional(readOnly = true)
    public List<ServiceResponse> getServices(
            UUID areaId,
            boolean activeOnly) {

        findArea(areaId);

        List<Service> services;

        if (activeOnly) {
            services = serviceRepository
                    .findAllByCruiseArea_IdAndStatusOrderByNameAsc(
                            areaId,
                            ServiceStatus.ACTIVE);
        } else {
            services = serviceRepository
                    .findAllByCruiseArea_IdOrderByNameAsc(
                            areaId);
        }

        return services.stream()
                .map(ServiceMapper::toResponse)
                .toList();
    }

    public ServiceResponse updateService(
            UUID areaId,
            UUID serviceId,
            UpdateServiceRequest request) {

        Service service = findById(areaId, serviceId);

        if (serviceRepository
                .existsByCruiseArea_IdAndNameIgnoreCaseAndIdNot(
                        areaId,
                        request.getName(),
                        serviceId)) {

            throw new AppException(
                    "Service name already exists in this area",
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
            UUID areaId,
            UUID serviceId) {

        Service service = findById(areaId, serviceId);

        if (service.getImagePublicId() != null
                && !service.getImagePublicId().isBlank()) {

            fileStorageService.delete(
                    service.getImagePublicId());
        }

        serviceRepository.delete(service);
    }

    private CruiseArea findArea(UUID areaId) {

        return cruiseAreaRepository
                .findById(areaId)
                .orElseThrow(() -> new AppException(
                        "Cruise area not found",
                        HttpStatus.NOT_FOUND));
    }

    private Service findById(
            UUID areaId,
            UUID serviceId) {

        return serviceRepository
                .findByIdAndCruiseArea_Id(
                        serviceId,
                        areaId)
                .orElseThrow(() -> new AppException(
                        "Service not found",
                        HttpStatus.NOT_FOUND));
    }
}