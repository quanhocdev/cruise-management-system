package com.project.tour.service.service;

import com.project.tour.dto.service.area.CreateServiceAreaRequest;
import com.project.tour.dto.service.area.ServiceAreaResponse;
import com.project.tour.dto.service.area.UpdateServiceAreaRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.service.ServiceAreaMapper;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.Service;
import com.project.tour.model.ServiceArea;
import com.project.tour.model.enums.ServiceStatus;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.repository.service.ServiceAreaRepository;
import com.project.tour.repository.service.ServiceRepository;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
@Transactional
public class ServiceAreaService {

    private final ServiceAreaRepository serviceAreaRepository;
    private final CruiseAreaRepository cruiseAreaRepository;
    private final ServiceRepository serviceRepository;

    public ServiceAreaService(
            ServiceAreaRepository serviceAreaRepository,
            CruiseAreaRepository cruiseAreaRepository,
            ServiceRepository serviceRepository) {

        this.serviceAreaRepository = serviceAreaRepository;
        this.cruiseAreaRepository = cruiseAreaRepository;
        this.serviceRepository = serviceRepository;
    }

    /*
     * =====================================================
     * CREATE
     * =====================================================
     */
    public ServiceAreaResponse createServiceArea(
            UUID areaId,
            CreateServiceAreaRequest request) {

        CruiseArea area = findArea(areaId);

        Service service = findService(
                request.getServiceId());

        if (service.getStatus() != ServiceStatus.ACTIVE) {
            throw new AppException(
                    "Cannot assign an inactive service",
                    HttpStatus.BAD_REQUEST);
        }

        if (serviceAreaRepository
                .existsByCruiseArea_IdAndService_Id(
                        areaId,
                        request.getServiceId())) {

            throw new AppException(
                    "Service is already assigned to this area",
                    HttpStatus.CONFLICT);
        }

        ServiceArea serviceArea = new ServiceArea();

        serviceArea.setCruiseArea(area);
        serviceArea.setService(service);

        ServiceArea saved =
                serviceAreaRepository.save(serviceArea);

        return ServiceAreaMapper.toResponse(saved);
    }

    /*
     * =====================================================
     * GET BY ID
     * =====================================================
     */
    @Transactional(readOnly = true)
    public ServiceAreaResponse getServiceAreaById(
            UUID areaId,
            UUID serviceAreaId) {

        return ServiceAreaMapper.toResponse(
                findServiceArea(
                        areaId,
                        serviceAreaId));
    }

    /*
     * =====================================================
     * GET BY AREA
     * =====================================================
     */
    @Transactional(readOnly = true)
    public List<ServiceAreaResponse> getServicesByArea(
            UUID areaId) {

        findArea(areaId);

        return serviceAreaRepository
                .findAllByCruiseArea_IdOrderByService_NameAsc(
                        areaId)
                .stream()
                .map(ServiceAreaMapper::toResponse)
                .toList();
    }

    /*
     * =====================================================
     * GET BY SERVICE
     * =====================================================
     */
    @Transactional(readOnly = true)
    public List<ServiceAreaResponse> getAreasByService(
            UUID serviceId) {

        findService(serviceId);

        return serviceAreaRepository
                .findAllByService_IdOrderByCruiseArea_NameAsc(
                        serviceId)
                .stream()
                .map(ServiceAreaMapper::toResponse)
                .toList();
    }

    /*
     * =====================================================
     * UPDATE
     * =====================================================
     */
    public ServiceAreaResponse updateServiceArea(
            UUID areaId,
            UUID serviceAreaId,
            UpdateServiceAreaRequest request) {

        ServiceArea serviceArea =
                findServiceArea(
                        areaId,
                        serviceAreaId);

        Service service =
                findService(
                        request.getServiceId());

        if (service.getStatus() != ServiceStatus.ACTIVE) {
            throw new AppException(
                    "Cannot assign an inactive service",
                    HttpStatus.BAD_REQUEST);
        }

        if (serviceAreaRepository
                .existsByCruiseArea_IdAndService_IdAndIdNot(
                        areaId,
                        request.getServiceId(),
                        serviceAreaId)) {

            throw new AppException(
                    "Service is already assigned to this area",
                    HttpStatus.CONFLICT);
        }

        serviceArea.setService(service);

        ServiceArea updated =
                serviceAreaRepository.save(serviceArea);

        return ServiceAreaMapper.toResponse(updated);
    }

    /*
     * =====================================================
     * DELETE
     * =====================================================
     */
    public void deleteServiceArea(
            UUID areaId,
            UUID serviceAreaId) {

        ServiceArea serviceArea =
                findServiceArea(
                        areaId,
                        serviceAreaId);

        serviceAreaRepository.delete(serviceArea);
    }

    /*
     * =====================================================
     * FIND AREA
     * =====================================================
     */
    private CruiseArea findArea(UUID areaId) {

        return cruiseAreaRepository
                .findById(areaId)
                .orElseThrow(() ->
                        new AppException(
                                "Cruise area not found",
                                HttpStatus.NOT_FOUND));
    }

    /*
     * =====================================================
     * FIND SERVICE
     * =====================================================
     */
    private Service findService(UUID serviceId) {

        return serviceRepository
                .findById(serviceId)
                .orElseThrow(() ->
                        new AppException(
                                "Service not found",
                                HttpStatus.NOT_FOUND));
    }

    /*
     * =====================================================
     * FIND SERVICE AREA
     * =====================================================
     */
    private ServiceArea findServiceArea(
            UUID areaId,
            UUID serviceAreaId) {

        return serviceAreaRepository
                .findByIdAndCruiseArea_Id(
                        serviceAreaId,
                        areaId)
                .orElseThrow(() ->
                        new AppException(
                                "Service assignment not found",
                                HttpStatus.NOT_FOUND));
    }
}