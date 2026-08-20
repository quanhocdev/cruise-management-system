package com.project.tour.service.tour.convenience;

import com.project.tour.dto.tour.convenience.service.ServiceTourConfigRequest;
import com.project.tour.dto.tour.convenience.service.ServiceTourResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.ServiceTourMapper;
import com.project.tour.model.Service;
import com.project.tour.model.ServiceTour;
import com.project.tour.model.enums.ServiceStatus;
import com.project.tour.model.enums.convenience.ServiceTourStatus;
import com.project.tour.repository.service.ServiceRepository;
import com.project.tour.repository.tour.ServiceTourAssignmentRepository;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@org.springframework.stereotype.Service
@Transactional
public class ServiceTourConfigService {

    private final ServiceTourAssignmentRepository serviceTourRepository;
    private final ServiceRepository serviceRepository;
    private final ServiceTourMapper serviceTourMapper;

    public ServiceTourConfigService(
            ServiceTourAssignmentRepository serviceTourRepository,
            ServiceRepository serviceRepository,
            ServiceTourMapper serviceTourMapper) {

        this.serviceTourRepository = serviceTourRepository;
        this.serviceRepository = serviceRepository;
        this.serviceTourMapper = serviceTourMapper;
    }

    // =====================================================
    // POST CONFIG
    // =====================================================

    /**
     * Cấu hình ServiceTour lần đầu.
     *
     * Điều kiện:
     * - WAITING_CONFIG
     * - Service phải ACTIVE
     *
     * Sau khi thành công:
     *
     * WAITING_CONFIG -> NOT_STARTED
     */
    public ServiceTourResponse configure(
            UUID assignmentId,
            ServiceTourConfigRequest request) {

        ServiceTour serviceTour = serviceTourRepository
                .findById(assignmentId)
                .orElseThrow(() -> new AppException(
                        "Service tour assignment not found",
                        HttpStatus.NOT_FOUND));

        if (serviceTour.getStatus() != ServiceTourStatus.WAITING_CONFIG) {

            throw new AppException(
                    "Service tour is not waiting for configuration",
                    HttpStatus.BAD_REQUEST);
        }

        Service service = getActiveService(request.serviceId());

        applyConfig(
                serviceTour,
                request,
                service);

        serviceTour.setStatus(ServiceTourStatus.NOT_STARTED);

        ServiceTour saved = serviceTourRepository.save(serviceTour);

        return serviceTourMapper.toResponse(saved);
    }

    // =====================================================
    // PATCH CONFIG
    // =====================================================

    /**
     * Cập nhật lại cấu hình ServiceTour.
     *
     * Chỉ cho phép khi:
     *
     * NOT_STARTED
     *
     * Trạng thái sau khi cập nhật:
     *
     * NOT_STARTED -> NOT_STARTED
     */
    public ServiceTourResponse updateConfig(
            UUID assignmentId,
            ServiceTourConfigRequest request) {

        ServiceTour serviceTour = serviceTourRepository
                .findById(assignmentId)
                .orElseThrow(() -> new AppException(
                        "Service tour assignment not found",
                        HttpStatus.NOT_FOUND));

        if (serviceTour.getStatus() != ServiceTourStatus.NOT_STARTED) {

            throw new AppException(
                    "Only NOT_STARTED service tour can be updated",
                    HttpStatus.BAD_REQUEST);
        }

        Service service = getActiveService(request.serviceId());

        applyConfig(
                serviceTour,
                request,
                service);

        serviceTour.setStatus(ServiceTourStatus.NOT_STARTED);

        ServiceTour saved = serviceTourRepository.save(serviceTour);

        return serviceTourMapper.toResponse(saved);
    }

    // =====================================================
    // APPLY CONFIG
    // =====================================================

    /**
     * Áp dụng thông tin cấu hình vào ServiceTour.
     */
    private void applyConfig(
            ServiceTour serviceTour,
            ServiceTourConfigRequest request,
            Service service) {

        serviceTour.setService(service);

        serviceTour.setMaxPassengers(
                request.maxPassengers());

        serviceTour.setDurationMinutes(
                request.durationMinutes());
    }

    // =====================================================
    // FIND ACTIVE SERVICE
    // =====================================================

    /**
     * Lấy Service và đảm bảo Service đang ACTIVE.
     */
    private Service getActiveService(UUID serviceId) {

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
}