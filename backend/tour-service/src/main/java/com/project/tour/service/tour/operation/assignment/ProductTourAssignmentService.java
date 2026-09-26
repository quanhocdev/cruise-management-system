package com.project.tour.service.tour.operation.assignment;

import com.project.tour.dto.tour.operation.ProductTourAssignmentRequest;
import com.project.tour.dto.tour.operation.ProductTourAssignmentResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.ProductTourAssignmentMapper;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.Tour;
import com.project.tour.model.convenience.product.ProductTour;
import com.project.tour.repository.convenience.ProductTourRepository;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.repository.tour.TourRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProductTourAssignmentService {

        private final ProductTourRepository productTourRepository;
        private final TourRepository tourRepository;
        private final CruiseAreaRepository cruiseAreaRepository;
        private final ProductTourAssignmentMapper assignmentMapper;

        public ProductTourAssignmentService(
                        ProductTourRepository productTourRepository,
                        TourRepository tourRepository,
                        CruiseAreaRepository cruiseAreaRepository,
                        ProductTourAssignmentMapper assignmentMapper) {

                this.productTourRepository = productTourRepository;
                this.tourRepository = tourRepository;
                this.cruiseAreaRepository = cruiseAreaRepository;
                this.assignmentMapper = assignmentMapper;
        }

        public ProductTourAssignmentResponse assign(
                        ProductTourAssignmentRequest request) {

                // 1. Kiểm tra Tour tồn tại
                Tour tour = tourRepository.findById(request.tourId())
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));

                // 2. Kiểm tra CruiseArea tồn tại
                CruiseArea cruiseArea = cruiseAreaRepository.findById(
                                request.cruiseAreaId())
                                .orElseThrow(() -> new AppException(
                                                "Cruise area not found",
                                                HttpStatus.NOT_FOUND));

                // 3. Validate CruiseArea
                if (cruiseArea.getStatus() == null) {
                        throw new AppException(
                                        "Cruise area status is invalid",
                                        HttpStatus.BAD_REQUEST);
                }

                if (cruiseArea.getCruiseDeck() == null) {
                        throw new AppException(
                                        "Cruise area is not assigned to a deck",
                                        HttpStatus.BAD_REQUEST);
                }

                // 4. Tour phải được gán Cruise
                if (tour.getCruise() == null) {
                        throw new AppException(
                                        "Tour has not been assigned to a cruise",
                                        HttpStatus.BAD_REQUEST);
                }

                // 5. CruiseArea phải thuộc đúng Cruise của Tour
                if (!tour.getCruise().getId()
                                .equals(cruiseArea.getCruiseDeck().getCruise().getId())) {

                        throw new AppException(
                                        "Cruise area does not belong to the cruise assigned to this tour",
                                        HttpStatus.BAD_REQUEST);
                }

                // 6. Kiểm tra ProductTour đã tồn tại chưa
                ProductTour productTour = productTourRepository
                                .findByTourIdAndCruiseAreaId(
                                                request.tourId(),
                                                request.cruiseAreaId())
                                .orElseGet(() -> {

                                        ProductTour newProductTour = new ProductTour();

                                        newProductTour.setTourId(
                                                        request.tourId());

                                        newProductTour.setCruiseAreaId(
                                                        request.cruiseAreaId());

                                        // Product chưa được cấu hình.
                                        // Entity mặc định là WAITING_CONFIG.
                                        return productTourRepository.save(
                                                        newProductTour);
                                });

                // 7. Trả response cho Operation
                return assignmentMapper.toResponse(
                                productTour,
                                tour,
                                cruiseArea);
        }

        /**
         * Lấy toàn bộ phân công Product của một Tour.
         */
        @Transactional(readOnly = true)
        public List<ProductTourAssignmentResponse> getByTour(
                        UUID tourId) {

                Tour tour = tourRepository.findById(tourId)
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));

                return productTourRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                                .stream()
                                .map(productTour -> {

                                        CruiseArea cruiseArea = cruiseAreaRepository
                                                        .findById(productTour.getCruiseAreaId())
                                                        .orElse(null);

                                        return assignmentMapper.toResponse(
                                                        productTour,
                                                        tour,
                                                        cruiseArea);
                                })
                                .toList();
        }

        /**
         * Xóa phân công Product.
         *
         * Chỉ xóa ProductTour trong DB của tour-service.
         * KHÔNG bắn Kafka.
         */
        @Transactional
        public void deleteAssignment(
                        UUID tourId,
                        UUID cruiseAreaId) {

                if (!productTourRepository
                                .existsByTourIdAndCruiseAreaId(
                                                tourId,
                                                cruiseAreaId)) {

                        throw new AppException(
                                        "Assignment not found",
                                        HttpStatus.NOT_FOUND);
                }

                productTourRepository.deleteByTourIdAndCruiseAreaId(
                                tourId,
                                cruiseAreaId);
        }
}