package com.project.tour.service.tour.operation;

import com.project.tour.dto.tour.operation.ActivityCruiseTourAssignmentResponse;
import com.project.tour.dto.tour.operation.OperationTourConfigurationResponse;
import com.project.tour.dto.tour.operation.ProductTourAssignmentResponse;
import com.project.tour.dto.tour.operation.ServiceTourAssignmentResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.ActivityCruiseTourAssignmentMapper;
import com.project.tour.mapper.tour.ProductTourAssignmentMapper;
import com.project.tour.mapper.tour.ServiceTourAssignmentMapper;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.Tour;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.repository.tour.ActivityCruiseTourAssignmentRepository;
import com.project.tour.repository.tour.AssignmentProductRepository;
import com.project.tour.repository.tour.AssignmentServiceRepository; // Đã đổi tên Repository
import com.project.tour.repository.tour.TourRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class OperationTourConfigurationService {

        private final TourRepository tourRepository;
        private final ActivityCruiseTourAssignmentRepository activityRepository;
        private final AssignmentProductRepository assignmentProductRepository;
        private final CruiseAreaRepository cruiseAreaRepository;
        private final AssignmentServiceRepository serviceRepository; // Đã sửa kiểu dữ liệu

        private final ProductTourAssignmentMapper productMapper;
        private final ServiceTourAssignmentMapper serviceMapper;

        public OperationTourConfigurationService(
                        TourRepository tourRepository,
                        ActivityCruiseTourAssignmentRepository activityRepository,
                        AssignmentProductRepository assignmentProductRepository,
                        CruiseAreaRepository cruiseAreaRepository,
                        AssignmentServiceRepository serviceRepository, // Đã sửa ở constructor
                        ProductTourAssignmentMapper productMapper,
                        ServiceTourAssignmentMapper serviceMapper) {

                this.tourRepository = tourRepository;
                this.activityRepository = activityRepository;
                this.assignmentProductRepository = assignmentProductRepository;
                this.cruiseAreaRepository = cruiseAreaRepository;
                this.serviceRepository = serviceRepository;
                this.productMapper = productMapper;
                this.serviceMapper = serviceMapper;
        }

        /**
         * Operation lấy toàn bộ cấu hình của một Tour.
         */
        public OperationTourConfigurationResponse getConfiguration(UUID tourId) {

                // =====================================================
                // TOUR
                // =====================================================
                Tour tour = tourRepository.findById(tourId)
                                .orElseThrow(() -> new AppException("Tour not found", HttpStatus.NOT_FOUND));

                // =====================================================
                // ACTIVITY
                // =====================================================
                List<ActivityCruiseTourAssignmentResponse> activities = activityRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                                .stream()
                                .map(ActivityCruiseTourAssignmentMapper::toResponse)
                                .toList();

                // =====================================================
                // PRODUCT (AssignmentProduct)
                // =====================================================
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

                // =====================================================
                // SERVICE (AssignmentService)
                // =====================================================
                List<ServiceTourAssignmentResponse> services = serviceRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                                .stream()
                                .map(assignment -> {
                                        CruiseArea cruiseArea = cruiseAreaRepository
                                                        .findById(assignment.getCruiseAreaId())
                                                        .orElse(null);
                                        return serviceMapper.toResponse(assignment, tour, cruiseArea); // Sửa mapper
                                                                                                       // truyền 3 tham
                                                                                                       // số
                                })
                                .toList();

                // =====================================================
                // CHECK COMPLETE
                // =====================================================
                boolean configurationComplete = isActivityConfigurationComplete(activities)
                                && isProductConfigurationComplete(products)
                                && isServiceConfigurationComplete(services);

                // =====================================================
                // RESPONSE
                // =====================================================
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
        // ACTIVITY VALIDATION
        // =====================================================
        private boolean isActivityConfigurationComplete(
                        List<ActivityCruiseTourAssignmentResponse> activities) {

                if (activities.isEmpty()) {
                        return false;
                }

                return activities.stream().allMatch(activity -> activity.activityCruiseId() != null
                                && activity.startTime() != null
                                && activity.endTime() != null
                                && activity.maxPassengers() != null
                                && activity.maxPassengers() > 0
                                && activity.price() != null
                                && activity.price().signum() >= 0);
        }

        // =====================================================
        // PRODUCT VALIDATION
        // =====================================================
        private boolean isProductConfigurationComplete(
                        List<ProductTourAssignmentResponse> products) {

                if (products.isEmpty()) {
                        return false;
                }

                return products.stream().allMatch(product -> product.id() != null && product.cruiseAreaId() != null);
        }

        // =====================================================
        // SERVICE VALIDATION
        // =====================================================
        private boolean isServiceConfigurationComplete(
                        List<ServiceTourAssignmentResponse> services) {

                if (services.isEmpty()) {
                        return false;
                }

                return services.stream().allMatch(service -> service.id() != null && service.cruiseAreaId() != null);
        }
}