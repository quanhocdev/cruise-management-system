package com.project.tour.service.tour.operation;

import com.project.tour.dto.tour.operation.ProductTourAssignmentRequest;
import com.project.tour.dto.tour.operation.ProductTourAssignmentResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.ProductTourAssignmentMapper;
import com.project.tour.model.AssignmentProduct;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.Tour;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.repository.tour.AssignmentProductRepository;
import com.project.tour.repository.tour.TourRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProductTourAssignmentService {

        private final AssignmentProductRepository assignmentRepository;
        private final TourRepository tourRepository;
        private final CruiseAreaRepository cruiseAreaRepository;
        private final ProductTourAssignmentMapper assignmentMapper;

        public ProductTourAssignmentService(
                        AssignmentProductRepository assignmentRepository,
                        TourRepository tourRepository,
                        CruiseAreaRepository cruiseAreaRepository,
                        ProductTourAssignmentMapper assignmentMapper) {

                this.assignmentRepository = assignmentRepository;
                this.tourRepository = tourRepository;
                this.cruiseAreaRepository = cruiseAreaRepository;
                this.assignmentMapper = assignmentMapper;
        }

        /**
         * Operation phân công một CruiseArea cho Tour.
         *
         * Chỉ lưu assignment vào DB của tour-service.
         * KHÔNG bắn Kafka.
         */
        public ProductTourAssignmentResponse assign(
                        ProductTourAssignmentRequest request) {

                // 1. Kiểm tra Tour tồn tại
                Tour tour = tourRepository.findById(request.tourId())
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));

                // 2. Kiểm tra CruiseArea tồn tại
                CruiseArea cruiseArea = cruiseAreaRepository.findById(request.cruiseAreaId())
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

                // 6. Kiểm tra assignment đã tồn tại chưa
                AssignmentProduct assignment = assignmentRepository
                                .findByTourIdAndCruiseAreaId(
                                                request.tourId(),
                                                request.cruiseAreaId())
                                .orElseGet(() -> {

                                        AssignmentProduct newAssignment = new AssignmentProduct(
                                                        request.tourId(),
                                                        request.cruiseAreaId());

                                        // CHỈ lưu vào DB của tour-service
                                        return assignmentRepository.save(newAssignment);
                                });

                // 7. Trả response cho Operation
                return assignmentMapper.toResponse(
                                assignment,
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

                return assignmentRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                                .stream()
                                .map(assignment -> {

                                        CruiseArea cruiseArea = cruiseAreaRepository
                                                        .findById(assignment.getCruiseAreaId())
                                                        .orElse(null);

                                        return assignmentMapper.toResponse(
                                                        assignment,
                                                        tour,
                                                        cruiseArea);
                                })
                                .toList();
        }

        /**
         * Xóa phân công Product.
         *
         * Chỉ xóa trong DB của tour-service.
         * KHÔNG bắn Kafka.
         */
        @Transactional
        public void deleteAssignment(
                        UUID tourId,
                        UUID cruiseAreaId) {

                if (!assignmentRepository
                                .existsByTourIdAndCruiseAreaId(
                                                tourId,
                                                cruiseAreaId)) {

                        throw new AppException(
                                        "Assignment not found",
                                        HttpStatus.NOT_FOUND);
                }

                // Chỉ xóa khỏi DB tour-service
                assignmentRepository.deleteByTourIdAndCruiseAreaId(
                                tourId,
                                cruiseAreaId);
        }
}