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
import com.project.tour.model.convenience.product.ProductTour;
import com.project.tour.model.convenience.service.ServiceTour;
import com.project.tour.repository.convenience.ProductTourRepository;
import com.project.tour.repository.convenience.ServiceTourRepository;
import com.project.tour.repository.cruise.CruiseAreaRepository;
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
        private final ProductTourRepository productTourRepository;
        private final ServiceTourRepository serviceTourRepository;
        private final CruiseAreaRepository cruiseAreaRepository;

        private final ProductTourAssignmentMapper productMapper;
        private final ServiceTourAssignmentMapper serviceMapper;

        public OperationTourConfigurationService(
                        TourRepository tourRepository,
                        ProductTourRepository productTourRepository,
                        ServiceTourRepository serviceTourRepository,
                        CruiseAreaRepository cruiseAreaRepository,
                        ProductTourAssignmentMapper productMapper,
                        ServiceTourAssignmentMapper serviceMapper) {

                this.tourRepository = tourRepository;
                this.productTourRepository = productTourRepository;
                this.serviceTourRepository = serviceTourRepository;
                this.cruiseAreaRepository = cruiseAreaRepository;
                this.productMapper = productMapper;
                this.serviceMapper = serviceMapper;
        }

        /**
         * Operation lấy toàn bộ cấu hình của một Tour.
         */
        public OperationTourConfigurationResponse getConfiguration(
                        UUID tourId) {

                // =====================================================
                // 1. TOUR
                // =====================================================

                Tour tour = tourRepository.findById(tourId)
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));

                // =====================================================
                // 2. ACTIVITY
                // =====================================================

                List<ActivityCruiseTourAssignmentResponse> activities = Collections.emptyList();

                // =====================================================
                // 3. PRODUCT
                // =====================================================

                List<ProductTourAssignmentResponse> products = productTourRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                                .stream()
                                .map(productTour -> {

                                        CruiseArea cruiseArea = cruiseAreaRepository
                                                        .findById(productTour.getCruiseAreaId())
                                                        .orElse(null);

                                        return productMapper.toResponse(
                                                        productTour,
                                                        tour,
                                                        cruiseArea);
                                })
                                .toList();

                // =====================================================
                // 4. SERVICE
                // =====================================================

                List<ServiceTourAssignmentResponse> services = serviceTourRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                                .stream()
                                .map(serviceTour -> {

                                        CruiseArea cruiseArea = cruiseAreaRepository
                                                        .findById(serviceTour.getCruiseAreaId())
                                                        .orElse(null);

                                        return serviceMapper.toResponse(
                                                        serviceTour,
                                                        tour,
                                                        cruiseArea);
                                })
                                .toList();

                // =====================================================
                // 5. CHECK COMPLETE
                // =====================================================

                boolean configurationComplete = isActivityConfigurationComplete(activities)
                                && isProductConfigurationComplete(products)
                                && isServiceConfigurationComplete(services);

                // =====================================================
                // 6. RESPONSE
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
        // PRIVATE VALIDATION
        // =====================================================

        private boolean isActivityConfigurationComplete(
                        List<ActivityCruiseTourAssignmentResponse> activities) {

                if (activities == null || activities.isEmpty()) {
                        return false;
                }

                return activities.stream()
                                .allMatch(activity -> activity.id() != null
                                                && activity.cruiseAreaId() != null);
        }

        private boolean isProductConfigurationComplete(
                        List<ProductTourAssignmentResponse> products) {

                if (products == null || products.isEmpty()) {
                        return false;
                }

                return products.stream()
                                .allMatch(product -> product.id() != null
                                                && product.cruiseAreaId() != null);
        }

        private boolean isServiceConfigurationComplete(
                        List<ServiceTourAssignmentResponse> services) {

                if (services == null || services.isEmpty()) {
                        return false;
                }

                return services.stream()
                                .allMatch(service -> service.id() != null
                                                && service.cruiseAreaId() != null);
        }
}