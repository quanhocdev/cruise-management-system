// src/main/java/com/project/tour/service/tour/operation/OperationTourConfigurationService.java

package com.project.tour.service.tour.operation;

import com.project.tour.dto.tour.operation.ActivityCruiseTourAssignmentResponse;
import com.project.tour.dto.tour.operation.OperationTourConfigurationResponse;
import com.project.tour.dto.tour.operation.ProductTourAssignmentResponse;
import com.project.tour.dto.tour.operation.ServiceTourAssignmentResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.ActivityCruiseTourAssignmentMapper;
import com.project.tour.mapper.tour.ProductTourAssignmentMapper;
import com.project.tour.mapper.tour.ServiceTourAssignmentMapper;
import com.project.tour.model.Tour;
import com.project.tour.repository.tour.ActivityCruiseTourAssignmentRepository;
import com.project.tour.repository.tour.ProductTourAssignmentRepository;
import com.project.tour.repository.tour.ServiceTourAssignmentRepository;
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

    private final ProductTourAssignmentRepository productRepository;

    private final ServiceTourAssignmentRepository serviceRepository;

    private final ProductTourAssignmentMapper productMapper;

    private final ServiceTourAssignmentMapper serviceMapper;

    public OperationTourConfigurationService(
            TourRepository tourRepository,
            ActivityCruiseTourAssignmentRepository activityRepository,
            ProductTourAssignmentRepository productRepository,
            ServiceTourAssignmentRepository serviceRepository,
            ProductTourAssignmentMapper productMapper,
            ServiceTourAssignmentMapper serviceMapper) {

        this.tourRepository = tourRepository;
        this.activityRepository = activityRepository;
        this.productRepository = productRepository;
        this.serviceRepository = serviceRepository;
        this.productMapper = productMapper;
        this.serviceMapper = serviceMapper;
    }

    /**
     * Operation lấy toàn bộ cấu hình của một Tour.
     *
     * KHÔNG lọc theo status.
     *
     * Bao gồm toàn bộ:
     * - ActivityCruiseTour
     * - ProductTour
     * - ServiceTour
     *
     * Mỗi assignment trả về status hiện tại để FE có thể hiển thị:
     * WAITING_CONFIG
     * NOT_STARTED
     * IN_PROGRESS
     * COMPLETED
     * ...
     */
    public OperationTourConfigurationResponse getConfiguration(
            UUID tourId) {

        // =====================================================
        // TOUR
        // =====================================================

        Tour tour = tourRepository
                .findById(tourId)
                .orElseThrow(() -> new AppException(
                        "Tour not found",
                        HttpStatus.NOT_FOUND));

        // =====================================================
        // ACTIVITY
        // =====================================================

        List<ActivityCruiseTourAssignmentResponse> activities = activityRepository
                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                .stream()
                .map(ActivityCruiseTourAssignmentMapper::toResponse)
                .toList();

        // =====================================================
        // PRODUCT
        // =====================================================

        List<ProductTourAssignmentResponse> products = productRepository
                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                .stream()
                .map(productMapper::toResponse)
                .toList();

        // =====================================================
        // SERVICE
        // =====================================================

        List<ServiceTourAssignmentResponse> services = serviceRepository
                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                .stream()
                .map(serviceMapper::toResponse)
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
    // ACTIVITY
    // =====================================================

    private boolean isActivityConfigurationComplete(
            List<ActivityCruiseTourAssignmentResponse> activities) {

        /*
         * Không có assignment thì chưa thể coi là hoàn tất.
         */
        if (activities.isEmpty()) {
            return false;
        }

        return activities.stream().allMatch(activity ->

        activity.activityCruiseId() != null

                && activity.startTime() != null

                && activity.endTime() != null

                && activity.maxPassengers() != null
                && activity.maxPassengers() > 0

                && activity.price() != null
                && activity.price().signum() >= 0);
    }

    // =====================================================
    // PRODUCT
    // =====================================================

    private boolean isProductConfigurationComplete(
            List<ProductTourAssignmentResponse> products) {

        /*
         * Không có assignment thì chưa thể coi là hoàn tất.
         */
        if (products.isEmpty()) {
            return false;
        }

        return products.stream().allMatch(product ->

        product.productId() != null

                && product.quantity() != null
                && product.quantity() > 0);
    }

    // =====================================================
    // SERVICE
    // =====================================================

    private boolean isServiceConfigurationComplete(
            List<ServiceTourAssignmentResponse> services) {

        /*
         * Không có assignment thì chưa thể coi là hoàn tất.
         */
        if (services.isEmpty()) {
            return false;
        }

        return services.stream().allMatch(service ->

        service.serviceId() != null

                && service.maxPassengers() != null
                && service.maxPassengers() > 0

        /*
         * durationMinutes CÓ THỂ NULL.
         *
         * null = không giới hạn.
         */
        );
    }
}