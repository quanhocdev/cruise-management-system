package com.project.convenience.service.service;

import com.project.convenience.dto.service.convenience.ServiceTourResponse;
import com.project.convenience.mapper.ServiceTourMapper;
import com.project.convenience.model.enums.ServiceTourStatus;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.convenience.repository.ServiceTourRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ServiceTourService {

        private final ServiceTourRepository serviceTourRepository;
        private final ServiceTourMapper serviceTourMapper;

        public ServiceTourService(
                        ServiceTourRepository serviceTourRepository,
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