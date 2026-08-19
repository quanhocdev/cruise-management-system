package com.project.tour.service.tour.operation;

import com.project.tour.dto.tour.operation.ProductTourAssignmentRequest;
import com.project.tour.dto.tour.operation.ProductTourAssignmentResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.ProductTourAssignmentMapper;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.ProductTour;
import com.project.tour.model.Tour;
import com.project.tour.model.enums.convenience.ProductTourStatus;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.repository.tour.ProductTourAssignmentRepository;
import com.project.tour.repository.tour.TourRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProductTourAssignmentService {

        private final ProductTourAssignmentRepository assignmentRepository;
        private final TourRepository tourRepository;
        private final CruiseAreaRepository cruiseAreaRepository;
        private final ProductTourAssignmentMapper assignmentMapper;

        public ProductTourAssignmentService(
                        ProductTourAssignmentRepository assignmentRepository,
                        TourRepository tourRepository,
                        CruiseAreaRepository cruiseAreaRepository,
                        ProductTourAssignmentMapper assignmentMapper) {

                this.assignmentRepository = assignmentRepository;
                this.tourRepository = tourRepository;
                this.cruiseAreaRepository = cruiseAreaRepository;
                this.assignmentMapper = assignmentMapper;
        }

        /**
         * Operation phân công một khu vực tiện ích/sản phẩm cho Tour.
         */
        public ProductTourAssignmentResponse assign(ProductTourAssignmentRequest request) {

                Tour tour = tourRepository.findById(request.tourId())
                                .orElseThrow(() -> new AppException("Tour not found", HttpStatus.NOT_FOUND));

                CruiseArea cruiseArea = cruiseAreaRepository.findById(request.cruiseAreaId())
                                .orElseThrow(() -> new AppException("Cruise area not found", HttpStatus.NOT_FOUND));

                if (cruiseArea.getStatus() == null) {
                        throw new AppException("Cruise area status is invalid", HttpStatus.BAD_REQUEST);
                }

                if (cruiseArea.getCruiseDeck() == null) {
                        throw new AppException("Cruise area is not assigned to a deck", HttpStatus.BAD_REQUEST);
                }

                if (tour.getCruise() == null) {
                        throw new AppException("Tour has not been assigned to a cruise", HttpStatus.BAD_REQUEST);
                }

                if (!tour.getCruise().getId().equals(cruiseArea.getCruiseDeck().getCruise().getId())) {
                        throw new AppException(
                                        "Cruise area does not belong to the cruise assigned to this tour",
                                        HttpStatus.BAD_REQUEST);
                }

                // Chống trùng lặp bản ghi: Nếu đã gán rồi thì giữ nguyên / trả về dữ liệu hiện
                // tại
                return assignmentRepository.findByTourIdAndCruiseAreaId(request.tourId(), request.cruiseAreaId())
                                .map(assignmentMapper::toResponse)
                                .orElseGet(() -> {
                                        ProductTour assignment = new ProductTour();
                                        assignment.setTour(tour);
                                        assignment.setCruiseArea(cruiseArea);
                                        assignment.setProduct(null);
                                        assignment.setStatus(ProductTourStatus.WAITING_CONFIG);

                                        ProductTour saved = assignmentRepository.save(assignment);
                                        return assignmentMapper.toResponse(saved);
                                });
        }

        /**
         * Lấy toàn bộ phân công tiện ích/sản phẩm của một Tour.
         */
        @Transactional(readOnly = true)
        public List<ProductTourAssignmentResponse> getByTour(UUID tourId) {

                if (!tourRepository.existsById(tourId)) {
                        throw new AppException("Tour not found", HttpStatus.NOT_FOUND);
                }

                return assignmentRepository.findAllByTourIdOrderByCreatedAtAsc(tourId)
                                .stream()
                                .map(assignmentMapper::toResponse)
                                .toList();
        }

        /**
         * Xóa phân công tiện ích/sản phẩm theo tourId và cruiseAreaId (Chuẩn hóa giống
         * Activity)
         */
        @Transactional
        public void deleteAssignment(UUID tourId, UUID cruiseAreaId) {

                ProductTour assignment = assignmentRepository.findByTourIdAndCruiseAreaId(tourId, cruiseAreaId)
                                .orElseThrow(() -> new AppException("Assignment not found", HttpStatus.NOT_FOUND));

                if (assignment.getStatus() != ProductTourStatus.WAITING_CONFIG) {
                        throw new AppException(
                                        "Cannot delete an assignment that has already been configured",
                                        HttpStatus.BAD_REQUEST);
                }

                assignmentRepository.deleteByTourIdAndCruiseAreaId(tourId, cruiseAreaId);
        }
}