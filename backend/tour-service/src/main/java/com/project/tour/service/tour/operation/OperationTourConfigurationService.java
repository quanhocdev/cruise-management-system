package com.project.tour.service.tour.operation;

import com.project.tour.dto.tour.operation.ActivityCruiseTourAssignmentResponse;
import com.project.tour.dto.tour.operation.OperationTourConfigurationResponse;
import com.project.tour.dto.tour.operation.ProductTourAssignmentResponse;
import com.project.tour.dto.tour.operation.ServiceTourAssignmentResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.ProductTourAssignmentMapper;
import com.project.tour.mapper.tour.ServiceTourAssignmentMapper;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.Tour;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.repository.tour.AssignmentProductRepository;
import com.project.tour.repository.tour.AssignmentServiceRepository;
import com.project.tour.repository.tour.TourRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class OperationTourConfigurationService {

        private final TourRepository tourRepository;
        private final AssignmentProductRepository assignmentProductRepository;
        private final AssignmentServiceRepository serviceRepository;
        private final CruiseAreaRepository cruiseAreaRepository;

        private final ProductTourAssignmentMapper productMapper;
        private final ServiceTourAssignmentMapper serviceMapper;

        public OperationTourConfigurationService(
                        TourRepository tourRepository,
                        AssignmentProductRepository assignmentProductRepository,
                        AssignmentServiceRepository serviceRepository,
                        CruiseAreaRepository cruiseAreaRepository,
                        ProductTourAssignmentMapper productMapper,
                        ServiceTourAssignmentMapper serviceMapper) {

                this.tourRepository = tourRepository;
                this.assignmentProductRepository = assignmentProductRepository;
                this.serviceRepository = serviceRepository;
                this.cruiseAreaRepository = cruiseAreaRepository;
                this.productMapper = productMapper;
                this.serviceMapper = serviceMapper;
        }

        /**
         * Operation lấy toàn bộ cấu hình của một Tour.
         */
        public OperationTourConfigurationResponse getConfiguration(UUID tourId) {

                // 1. TOUR
                Tour tour = tourRepository.findById(tourId)
                                .orElseThrow(() -> new AppException("Tour not found", HttpStatus.NOT_FOUND));

                // 2. ACTIVITY
                // Vì Activity chuyển sang activity-cruise-service qua Event, tour-service tạm
                // trả list rỗng
                List<ActivityCruiseTourAssignmentResponse> activities = Collections.emptyList();

                // 3. PRODUCT (Giữ nguyên logic query từ DB tour-service)
                List<ProductTourAssignmentResponse> products = assignmentProductRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                                .stream()
                                .map(assignment -> {
                                        CruiseArea cruiseArea = cruiseAreaRepository
                                                        .findById(assignment.getCruiseAreaId())
                                                        .orElse(null);
                                        return productMapper.toResponse(assignment, tour, cruiseArea);
                                })
                                .toList();

                // 4. SERVICE (Giữ nguyên logic query từ DB tour-service)
                List<ServiceTourAssignmentResponse> services = serviceRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                                .stream()
                                .map(assignment -> {
                                        CruiseArea cruiseArea = cruiseAreaRepository
                                                        .findById(assignment.getCruiseAreaId())
                                                        .orElse(null);
                                        return serviceMapper.toResponse(assignment, tour, cruiseArea);
                                })
                                .toList();

                // 5. CHECK COMPLETE
                boolean configurationComplete = isActivityConfigurationComplete(activities)
                                && isProductConfigurationComplete(products)
                                && isServiceConfigurationComplete(services);

                // 6. RESPONSE (Trả đúng DTO FE đang chờ)
                return new OperationTourConfigurationResponse(
                                tour.getId(),
                                tour.getCode(),
                                tour.getName(),
                                activities,
                                products,
                                services,
                                configurationComplete);
        }

        // =====================================================
        // PRIVATE VALIDATION (Chỉ dùng nội bộ Backend)
        // =====================================================
        private boolean isActivityConfigurationComplete(
                        List<ActivityCruiseTourAssignmentResponse> activities) {

                if (activities == null || activities.isEmpty()) {
                        return false;
                }

                return activities.stream()
                                .allMatch(activity -> activity.id() != null && activity.cruiseAreaId() != null);
        }

        private boolean isProductConfigurationComplete(
                        List<ProductTourAssignmentResponse> products) {

                if (products == null || products.isEmpty()) {
                        return false;
                }

                return products.stream()
                                .allMatch(product -> product.id() != null && product.cruiseAreaId() != null);
        }

        private boolean isServiceConfigurationComplete(
                        List<ServiceTourAssignmentResponse> services) {

                if (services == null || services.isEmpty()) {
                        return false;
                }

                return services.stream()
                                .allMatch(service -> service.id() != null && service.cruiseAreaId() != null);
        }
}