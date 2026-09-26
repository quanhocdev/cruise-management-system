package com.project.tour.service.tour.operation.assignment;

import com.project.tour.dto.tour.operation.ActivityCruiseTourAssignmentRequest;
import com.project.tour.dto.tour.operation.ActivityCruiseTourAssignmentResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.ActivityCruiseTourAssignmentMapper;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.Tour;
import com.project.tour.model.activitycruise.ActivityCruiseTour;
import com.project.tour.model.activitycruise.enums.ActivityCruiseTourStatus;
import com.project.tour.repository.activitycruise.ActivityCruiseTourAssignmentRepository;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.repository.tour.TourRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ActivityCruiseTourAssignmentService {

        private final ActivityCruiseTourAssignmentRepository assignmentRepository;
        private final TourRepository tourRepository;
        private final CruiseAreaRepository cruiseAreaRepository;

        public ActivityCruiseTourAssignmentService(
                        ActivityCruiseTourAssignmentRepository assignmentRepository,
                        TourRepository tourRepository,
                        CruiseAreaRepository cruiseAreaRepository) {

                this.assignmentRepository = assignmentRepository;
                this.tourRepository = tourRepository;
                this.cruiseAreaRepository = cruiseAreaRepository;
        }

        public ActivityCruiseTourAssignmentResponse assign(
                        ActivityCruiseTourAssignmentRequest request) {

                // =====================================================
                // 1. KIỂM TRA TOUR
                // =====================================================

                Tour tour = tourRepository.findById(request.tourId())
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));

                // =====================================================
                // 2. KIỂM TRA CRUISE AREA
                // =====================================================

                CruiseArea cruiseArea = cruiseAreaRepository
                                .findById(request.cruiseAreaId())
                                .orElseThrow(() -> new AppException(
                                                "Cruise area not found",
                                                HttpStatus.NOT_FOUND));

                // =====================================================
                // 3. VALIDATE CRUISE AREA
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
                // 4. TOUR PHẢI ĐƯỢC GÁN CRUISE
                // =====================================================

                if (tour.getCruise() == null) {
                        throw new AppException(
                                        "Tour has not been assigned to a cruise",
                                        HttpStatus.BAD_REQUEST);
                }

                // =====================================================
                // 5. CRUISE AREA PHẢI THUỘC CÙNG CRUISE
                // =====================================================

                if (!tour.getCruise().getId()
                                .equals(cruiseArea.getCruiseDeck().getCruise().getId())) {

                        throw new AppException(
                                        "Cruise area does not belong to the cruise assigned to this tour",
                                        HttpStatus.BAD_REQUEST);
                }

                // =====================================================
                // 6. CHỐNG PHÂN CÔNG TRÙNG
                // =====================================================

                ActivityCruiseTour assignment = assignmentRepository
                                .findByTourIdAndCruiseAreaId(
                                                request.tourId(),
                                                request.cruiseAreaId())
                                .orElseGet(() -> {

                                        ActivityCruiseTour newAssignment = new ActivityCruiseTour();

                                        newAssignment.setTourId(
                                                        request.tourId());

                                        newAssignment.setCruiseAreaId(
                                                        request.cruiseAreaId());

                                        newAssignment.setStatus(
                                                        ActivityCruiseTourStatus.WAITING_CONFIG);

                                        return assignmentRepository.save(
                                                        newAssignment);
                                });

                // =====================================================
                // 7. TRẢ RESPONSE
                // =====================================================

                return ActivityCruiseTourAssignmentMapper.toResponse(
                                assignment,
                                tour,
                                cruiseArea);
        }

        /**
         * Lấy toàn bộ phân công Activity Cruise của một Tour.
         */
        @Transactional(readOnly = true)
        public List<ActivityCruiseTourAssignmentResponse> getByTour(
                        UUID tourId) {

                // =====================================================
                // 1. KIỂM TRA TOUR
                // =====================================================

                Tour tour = tourRepository.findById(tourId)
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));

                // =====================================================
                // 2. LẤY ASSIGNMENT
                // =====================================================

                return assignmentRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                                .stream()
                                .map(assignment -> {

                                        CruiseArea cruiseArea = cruiseAreaRepository
                                                        .findById(
                                                                        assignment.getCruiseAreaId())
                                                        .orElse(null);

                                        return ActivityCruiseTourAssignmentMapper.toResponse(
                                                        assignment,
                                                        tour,
                                                        cruiseArea);
                                })
                                .toList();
        }

        public void deleteAssignment(
                        UUID tourId,
                        UUID cruiseAreaId) {

                // =====================================================
                // 1. KIỂM TRA TOUR
                // =====================================================

                if (!tourRepository.existsById(tourId)) {
                        throw new AppException(
                                        "Tour not found",
                                        HttpStatus.NOT_FOUND);
                }

                // =====================================================
                // 2. KIỂM TRA CRUISE AREA
                // =====================================================

                if (!cruiseAreaRepository.existsById(cruiseAreaId)) {
                        throw new AppException(
                                        "Cruise area not found",
                                        HttpStatus.NOT_FOUND);
                }

                // =====================================================
                // 3. KIỂM TRA ASSIGNMENT
                // =====================================================

                if (!assignmentRepository.existsByTourIdAndCruiseAreaId(
                                tourId,
                                cruiseAreaId)) {

                        throw new AppException(
                                        "Assignment not found",
                                        HttpStatus.NOT_FOUND);
                }

                // =====================================================
                // 4. XÓA
                // =====================================================

                assignmentRepository.deleteByTourIdAndCruiseAreaId(
                                tourId,
                                cruiseAreaId);
        }
}
