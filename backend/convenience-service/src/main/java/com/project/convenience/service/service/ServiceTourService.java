package com.project.convenience.service.service;

import com.project.convenience.dto.service.convenience.ServiceTourResponse;
import com.project.convenience.mapper.ServiceTourMapper;
import com.project.convenience.model.ServiceTour;
import com.project.convenience.model.enums.ServiceTourStatus;
import com.project.convenience.repository.ServiceTourRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
        // Xử lý Event CREATE từ Kafka
        // =====================================================
        public void createServiceTourFromEvent(UUID tourId, UUID cruiseAreaId) {
                // 1. Kiểm tra cặp (tourId, cruiseAreaId) đã tồn tại chưa
                boolean exists = serviceTourRepository.findByTourIdAndCruiseAreaId(tourId, cruiseAreaId).isPresent();
                if (exists) {
                        return; // Bỏ qua nếu đã tồn tại (chống duplicate khi Kafka retry)
                }

                // 2. Khởi tạo Entity ServiceTour mới ở trạng thái WAITING_CONFIG
                ServiceTour serviceTour = new ServiceTour();
                serviceTour.setTourId(tourId);
                serviceTour.setCruiseAreaId(cruiseAreaId);
                serviceTour.setStatus(ServiceTourStatus.WAITING_CONFIG);

                // 3. Lưu DB
                serviceTourRepository.save(serviceTour);
        }

        // =====================================================
        // Xử lý Event DELETE từ Kafka
        // =====================================================
        public void deleteServiceTourFromEvent(UUID tourId, UUID cruiseAreaId) {
                serviceTourRepository.findByTourIdAndCruiseAreaId(tourId, cruiseAreaId)
                                .ifPresent(serviceTourRepository::delete);
        }

        // =====================================================
        // GET CONFIGURABLE SERVICES
        // =====================================================
        /**
         * Lấy danh sách các ServiceTour đang chờ cấu hình
         */
        @Transactional(readOnly = true)
        public List<ServiceTourResponse> getPendingConfig() {

                return serviceTourRepository
                                .findConfigurable(
                                                List.of(ServiceTourStatus.WAITING_CONFIG))
                                .stream()
                                .map(serviceTourMapper::toResponse)
                                .toList();
        }
}