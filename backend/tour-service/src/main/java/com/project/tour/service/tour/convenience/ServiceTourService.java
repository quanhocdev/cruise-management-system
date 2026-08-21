package com.project.tour.service.tour.convenience;

import com.project.tour.dto.tour.convenience.service.ServiceTourResponse;
import com.project.tour.mapper.tour.ServiceTourMapper;
import com.project.tour.model.enums.convenience.ServiceTourStatus;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.repository.tour.ServiceTourAssignmentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ServiceTourService {

        private final ServiceTourAssignmentRepository serviceTourRepository;
        private final ServiceTourMapper serviceTourMapper;

        public ServiceTourService(
                        ServiceTourAssignmentRepository serviceTourRepository,
                        ServiceTourMapper serviceTourMapper) {

                this.serviceTourRepository = serviceTourRepository;
                this.serviceTourMapper = serviceTourMapper;
        }

        // =====================================================
        // GET CONFIGURABLE SERVICES
        // =====================================================

        /**
         * Lấy các ServiceTour mà Convenience có thể cấu hình
         * hoặc chỉnh sửa.
         *
         * Điều kiện:
         *
         * Tour.statusTrip = APPROVED
         *
         * ServiceTour.status:
         * - WAITING_CONFIG
         * - NOT_STARTED
         */
        @Transactional(readOnly = true)
        public List<ServiceTourResponse> getPendingConfig() {

                return serviceTourRepository
                                .findConfigurable(
                                                TourStatusTrip.APPROVED,
                                                List.of(
                                                                ServiceTourStatus.WAITING_CONFIG,
                                                                ServiceTourStatus.NOT_STARTED))
                                .stream()
                                .map(serviceTourMapper::toResponse)
                                .toList();
        }
}