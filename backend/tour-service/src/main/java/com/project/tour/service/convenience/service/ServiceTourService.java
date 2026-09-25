package com.project.tour.service.convenience.service;

import com.project.tour.dto.convenience.service.convenience.ServiceTourConfigRequest;
import com.project.tour.dto.convenience.service.convenience.ServiceTourResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.convenience.ServiceTourMapper;
import com.project.tour.model.convenience.enums.ServiceStatus;
import com.project.tour.model.convenience.enums.ServiceTourStatus;
import com.project.tour.model.convenience.service.Service;
import com.project.tour.model.convenience.service.ServiceTour;
import com.project.tour.repository.convenience.ServiceRepository;
import com.project.tour.repository.convenience.ServiceTourRepository;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
@Transactional
public class ServiceTourService {

    private final ServiceTourRepository serviceTourRepository;
    private final ServiceRepository serviceRepository;
    private final ServiceTourMapper mapper;

    public ServiceTourService(
            ServiceTourRepository serviceTourRepository,
            ServiceRepository serviceRepository,
            ServiceTourMapper mapper) {

        this.serviceTourRepository = serviceTourRepository;
        this.serviceRepository = serviceRepository;
        this.mapper = mapper;
    }

    // =====================================================
    // GET ALL
    // =====================================================

    @Transactional(readOnly = true)
    public List<ServiceTourResponse> getAllAssignments() {

        return serviceTourRepository
                .findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    // =====================================================
    // GET PENDING CONFIG
    // =====================================================

    @Transactional(readOnly = true)
    public List<ServiceTourResponse> getPendingConfig() {

        return serviceTourRepository
                .findConfigurable(
                        List.of(
                                ServiceTourStatus.WAITING_CONFIG))
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    // =====================================================
    // GET BY TOUR
    // =====================================================

    @Transactional(readOnly = true)
    public List<ServiceTourResponse> getByTour(
            UUID tourId) {

        return serviceTourRepository
                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    // =====================================================
    // CONFIGURE
    // =====================================================

    /**
     * Cấu hình ServiceTour.
     *
     * WAITING_CONFIG
     * ↓
     * WAITING_CONFIG
     *
     * Save/Edit chỉ lưu cấu hình.
     * Chưa hoàn tất cấu hình.
     */
    public ServiceTourResponse configure(
            UUID serviceTourId,
            ServiceTourConfigRequest request) {

        ServiceTour serviceTour = findServiceTour(serviceTourId);

        if (serviceTour.getStatus() != ServiceTourStatus.WAITING_CONFIG) {

            throw new AppException(
                    "Service tour is not waiting for configuration",
                    HttpStatus.CONFLICT);
        }

        Service service = getActiveService(
                request.serviceId());

        applyConfig(
                serviceTour,
                request,
                service);

        ServiceTour saved = serviceTourRepository.save(serviceTour);

        return mapper.toResponse(saved);
    }

    // =====================================================
    // UPDATE CONFIG
    // =====================================================

    /**
     * Cập nhật cấu hình ServiceTour
     * khi vẫn đang WAITING_CONFIG.
     */
    public ServiceTourResponse updateConfig(
            UUID serviceTourId,
            ServiceTourConfigRequest request) {

        ServiceTour serviceTour = findServiceTour(serviceTourId);

        if (serviceTour.getStatus() != ServiceTourStatus.WAITING_CONFIG) {

            throw new AppException(
                    "Service tour configuration has already been completed and cannot be modified",
                    HttpStatus.CONFLICT);
        }

        Service service = getActiveService(
                request.serviceId());

        applyConfig(
                serviceTour,
                request,
                service);

        ServiceTour saved = serviceTourRepository.save(serviceTour);

        return mapper.toResponse(saved);
    }

    // =====================================================
    // COMPLETE CONFIGURATION
    // =====================================================

    /**
     * Hoàn tất cấu hình toàn bộ ServiceTour
     * của một Tour.
     *
     * WAITING_CONFIG
     * ↓
     * CONFIGURED
     */
    public void completeConfiguration(
            UUID tourId) {

        List<ServiceTour> serviceTours = serviceTourRepository
                .findAllByTourIdOrderByCreatedAtAsc(
                        tourId);

        if (serviceTours.isEmpty()) {

            throw new AppException(
                    "No service tour configuration found for tour",
                    HttpStatus.NOT_FOUND);
        }

        for (ServiceTour serviceTour : serviceTours) {

            if (serviceTour.getStatus() != ServiceTourStatus.WAITING_CONFIG) {

                throw new AppException(
                        "Service tour configuration has already been completed or is not in a valid state",
                        HttpStatus.CONFLICT);
            }

            if (serviceTour.getService() == null) {

                throw new AppException(
                        "Service configuration is missing",
                        HttpStatus.BAD_REQUEST);
            }

            if (serviceTour.getMaxPassengers() == null
                    || serviceTour.getMaxPassengers() <= 0) {

                throw new AppException(
                        "Maximum passengers is invalid",
                        HttpStatus.BAD_REQUEST);
            }

            if (serviceTour.getDurationMinutes() != null
                    && serviceTour.getDurationMinutes() <= 0) {

                throw new AppException(
                        "Duration is invalid",
                        HttpStatus.BAD_REQUEST);
            }
        }

        for (ServiceTour serviceTour : serviceTours) {

            serviceTour.setStatus(
                    ServiceTourStatus.CONFIGURED);
        }

        serviceTourRepository.saveAll(serviceTours);
    }

    // =====================================================
    // APPLY CONFIG
    // =====================================================

    private void applyConfig(
            ServiceTour serviceTour,
            ServiceTourConfigRequest request,
            Service service) {

        serviceTour.setService(service);

        serviceTour.setMaxPassengers(
                request.maxPassengers());

        serviceTour.setDurationMinutes(
                request.durationMinutes());

        // Không đổi status.
    }

    // =====================================================
    // FIND ACTIVE SERVICE
    // =====================================================

    private Service getActiveService(
            UUID serviceId) {

        Service service = serviceRepository
                .findById(serviceId)
                .orElseThrow(() -> new AppException(
                        "Service not found",
                        HttpStatus.NOT_FOUND));

        if (service.getStatus() != ServiceStatus.ACTIVE) {

            throw new AppException(
                    "Service is not active",
                    HttpStatus.BAD_REQUEST);
        }

        return service;
    }

    // =====================================================
    // FIND SERVICE TOUR
    // =====================================================

    private ServiceTour findServiceTour(
            UUID serviceTourId) {

        return serviceTourRepository
                .findById(serviceTourId)
                .orElseThrow(() -> new AppException(
                        "Service tour not found",
                        HttpStatus.NOT_FOUND));
    }
}