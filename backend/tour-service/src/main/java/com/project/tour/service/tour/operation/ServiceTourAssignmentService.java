package com.project.tour.service.tour.operation;

import com.project.tour.dto.tour.operation.ServiceTourAssignmentRequest;
import com.project.tour.dto.tour.operation.ServiceTourAssignmentResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.ServiceTourAssignmentMapper;
import com.project.tour.model.AssignmentService;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.Tour;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.repository.tour.AssignmentServiceRepository;
import com.project.tour.repository.tour.TourRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ServiceTourAssignmentService {

        private final AssignmentServiceRepository assignmentRepository;
        private final TourRepository tourRepository;
        private final CruiseAreaRepository cruiseAreaRepository;
        private final ServiceTourAssignmentMapper assignmentMapper;

        public ServiceTourAssignmentService(
                        AssignmentServiceRepository assignmentRepository,
                        TourRepository tourRepository,
                        CruiseAreaRepository cruiseAreaRepository,
                        ServiceTourAssignmentMapper assignmentMapper) {

                this.assignmentRepository = assignmentRepository;
                this.tourRepository = tourRepository;
                this.cruiseAreaRepository = cruiseAreaRepository;
                this.assignmentMapper = assignmentMapper;
        }

        /**
         * Operation phân công một khu vực dịch vụ cho Tour.
         *
         * Chỉ lưu assignment vào DB của tour-service.
         *
         * Chưa bắn Kafka.
         * Kafka chỉ được bắn khi Tour được APPROVED.
         */
        public ServiceTourAssignmentResponse assign(
                        ServiceTourAssignmentRequest request) {

                // =====================================================
                // 1. Kiểm tra Tour
                // =====================================================

                Tour tour = tourRepository.findById(request.tourId())
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));

                // =====================================================
                // 2. Kiểm tra CruiseArea
                // =====================================================

                CruiseArea cruiseArea = cruiseAreaRepository.findById(request.cruiseAreaId())
                                .orElseThrow(() -> new AppException(
                                                "Cruise area not found",
                                                HttpStatus.NOT_FOUND));

                // =====================================================
                // 3. Validate CruiseArea
                // =====================================================

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

                // =====================================================
                // 4. Tour phải được gán Cruise
                // =====================================================

                if (tour.getCruise() == null) {
                        throw new AppException(
                                        "Tour has not been assigned to a cruise",
                                        HttpStatus.BAD_REQUEST);
                }

                // =====================================================
                // 5. CruiseArea phải thuộc cùng Cruise với Tour
                // =====================================================

                if (!tour.getCruise().getId()
                                .equals(cruiseArea.getCruiseDeck().getCruise().getId())) {

                        throw new AppException(
                                        "Cruise area does not belong to the cruise assigned to this tour",
                                        HttpStatus.BAD_REQUEST);
                }

                // =====================================================
                // 6. Chống phân công trùng
                // =====================================================

                AssignmentService assignment = assignmentRepository
                                .findByTourIdAndCruiseAreaId(
                                                request.tourId(),
                                                request.cruiseAreaId())
                                .orElseGet(() -> {

                                        AssignmentService newAssignment = new AssignmentService(
                                                        request.tourId(),
                                                        request.cruiseAreaId());

                                        // Chỉ lưu vào DB của tour-service.
                                        return assignmentRepository.save(newAssignment);
                                });

                // =====================================================
                // 7. Trả response
                // =====================================================

                return assignmentMapper.toResponse(
                                assignment,
                                tour,
                                cruiseArea);
        }

        /**
         * Lấy toàn bộ phân công dịch vụ của một Tour.
         */
        @Transactional(readOnly = true)
        public List<ServiceTourAssignmentResponse> getByTour(UUID tourId) {

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
         * Xóa phân công dịch vụ.
         *
         * Chỉ xóa trong DB của tour-service.
         *
         * Không bắn Kafka.
         */
        @Transactional
        public void deleteAssignment(
                        UUID tourId,
                        UUID cruiseAreaId) {

                // =====================================================
                // 1. Kiểm tra assignment tồn tại
                // =====================================================

                if (!assignmentRepository.existsByTourIdAndCruiseAreaId(
                                tourId,
                                cruiseAreaId)) {

                        throw new AppException(
                                        "Assignment not found",
                                        HttpStatus.NOT_FOUND);
                }

                // =====================================================
                // 2. Xóa assignment
                // =====================================================

                assignmentRepository.deleteByTourIdAndCruiseAreaId(
                                tourId,
                                cruiseAreaId);
        }
}