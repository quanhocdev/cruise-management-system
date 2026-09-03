package com.project.tour.service.tour.operation.assignment;

import com.project.tour.dto.tour.operation.ActivityCruiseTourAssignmentRequest;
import com.project.tour.dto.tour.operation.ActivityCruiseTourAssignmentResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.ActivityCruiseTourAssignmentMapper;
import com.project.tour.model.AssignmentActivityCruise;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.Tour;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.repository.tour.AssignmentActivityCruiseRepository;
import com.project.tour.repository.tour.TourRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ActivityCruiseTourAssignmentService {

        private final AssignmentActivityCruiseRepository assignmentRepository;
        private final TourRepository tourRepository;
        private final CruiseAreaRepository cruiseAreaRepository;

        public ActivityCruiseTourAssignmentService(
                        AssignmentActivityCruiseRepository assignmentRepository,
                        TourRepository tourRepository,
                        CruiseAreaRepository cruiseAreaRepository) {

                this.assignmentRepository = assignmentRepository;
                this.tourRepository = tourRepository;
                this.cruiseAreaRepository = cruiseAreaRepository;
        }

        /**
         * Operation phân công một CruiseArea cho Activity của Tour.
         *
         * Chỉ lưu assignment vào DB của tour-service.
         * Chưa bắn Kafka.
         */
        public ActivityCruiseTourAssignmentResponse assign(
                        ActivityCruiseTourAssignmentRequest request) {

                // 1. Kiểm tra Tour
                Tour tour = tourRepository.findById(request.tourId())
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));

                // 2. Kiểm tra CruiseArea
                CruiseArea cruiseArea = cruiseAreaRepository
                                .findById(request.cruiseAreaId())
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

                // 5. CruiseArea phải thuộc cùng Cruise với Tour
                if (!tour.getCruise().getId()
                                .equals(cruiseArea.getCruiseDeck().getCruise().getId())) {

                        throw new AppException(
                                        "Cruise area does not belong to the cruise assigned to this tour",
                                        HttpStatus.BAD_REQUEST);
                }

                // 6. Chống phân công trùng
                AssignmentActivityCruise assignment = assignmentRepository
                                .findByTourIdAndCruiseAreaId(
                                                request.tourId(),
                                                request.cruiseAreaId())
                                .orElseGet(() -> {

                                        AssignmentActivityCruise newAssignment = new AssignmentActivityCruise(
                                                        request.tourId(),
                                                        request.cruiseAreaId());

                                        return assignmentRepository.save(newAssignment);
                                });

                return ActivityCruiseTourAssignmentMapper.toResponse(
                                assignment,
                                tour,
                                cruiseArea);
        }

        /**
         * Lấy toàn bộ phân công Activity của một Tour.
         */
        @Transactional(readOnly = true)
        public List<ActivityCruiseTourAssignmentResponse> getByTour(
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

                                        return ActivityCruiseTourAssignmentMapper.toResponse(
                                                        assignment,
                                                        tour,
                                                        cruiseArea);
                                })
                                .toList();
        }

        /**
         * Xóa phân công Activity.
         *
         * Chỉ xóa trong DB của tour-service.
         * Không bắn Kafka.
         */
        public void deleteAssignment(
                        UUID tourId,
                        UUID cruiseAreaId) {

                if (!tourRepository.existsById(tourId)) {
                        throw new AppException(
                                        "Tour not found",
                                        HttpStatus.NOT_FOUND);
                }

                if (!cruiseAreaRepository.existsById(cruiseAreaId)) {
                        throw new AppException(
                                        "Cruise area not found",
                                        HttpStatus.NOT_FOUND);
                }

                if (!assignmentRepository.existsByTourIdAndCruiseAreaId(
                                tourId,
                                cruiseAreaId)) {

                        throw new AppException(
                                        "Assignment not found",
                                        HttpStatus.NOT_FOUND);
                }

                assignmentRepository.deleteByTourIdAndCruiseAreaId(
                                tourId,
                                cruiseAreaId);
        }
}