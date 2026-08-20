package com.project.tour.service.tour.convenience;

import com.project.tour.dto.tour.convenience.service.ServiceTourResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.ServiceTourMapper;
import com.project.tour.model.enums.convenience.ServiceTourStatus;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.repository.tour.ServiceTourAssignmentRepository;
import com.project.tour.repository.tour.TourRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ServiceTourService {

    private final ServiceTourAssignmentRepository serviceTourRepository;
    private final TourRepository tourRepository;
    private final ServiceTourMapper serviceTourMapper;

    public ServiceTourService(
            ServiceTourAssignmentRepository serviceTourRepository,
            TourRepository tourRepository,
            ServiceTourMapper serviceTourMapper) {

        this.serviceTourRepository = serviceTourRepository;
        this.tourRepository = tourRepository;
        this.serviceTourMapper = serviceTourMapper;
    }

    /**
     * Lấy các ServiceTour đang chờ Convenience cấu hình.
     *
     * Điều kiện:
     * - Tour phải APPROVED
     * - ServiceTour phải WAITING_CONFIG
     */
    @Transactional(readOnly = true)
    public List<ServiceTourResponse> getPendingConfig() {

        return serviceTourRepository
                .findAllByStatusOrderByCreatedAtAsc(
                        ServiceTourStatus.WAITING_CONFIG)
                .stream()
                .filter(serviceTour -> serviceTour.getTour() != null
                        && serviceTour.getTour().getStatusTrip() == TourStatusTrip.APPROVED)
                .map(serviceTourMapper::toResponse)
                .toList();
    }
}