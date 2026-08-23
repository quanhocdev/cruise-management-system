package com.project.tour.service.tour.operation;

import com.project.tour.dto.tour.operation.ActivityCruiseTourAssignmentRequest;
import com.project.tour.dto.tour.operation.ActivityCruiseTourAssignmentResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.ActivityCruiseTourAssignmentMapper;
import com.project.tour.model.ActivityCruiseTour;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.Tour;
import com.project.tour.model.enums.onboard.ActivityCruiseTourStatus;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.repository.tour.ActivityCruiseTourAssignmentRepository;
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

        /**
         * Operation phân công một khu vực cho Tour.
         *
         * Chỉ tạo assignment.
         *
         * activityCruise = null
         * startTime = null
         * endTime = null
         * maxPassengers = null
         * price = null
         *
         * status = WAITING_CONFIG
         */
        public ActivityCruiseTourAssignmentResponse assign(
                        ActivityCruiseTourAssignmentRequest request) {

                Tour tour = tourRepository
                                .findById(request.tourId())
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));

                // NẾU TOUR CẦN Ở TRẠNG THÁI PENDING HOẶC APPROVED THÌ CÓ THỂ BỎ BỚT HOẶC ĐIỀU
                // CHỈNH CHECK STATUS Ở ĐÂY
                // Ví dụ: Cho phép phân công khi Tour đang chờ duyệt hoặc đã duyệt

                CruiseArea cruiseArea = cruiseAreaRepository
                                .findById(request.cruiseAreaId())
                                .orElseThrow(() -> new AppException(
                                                "Cruise area not found",
                                                HttpStatus.NOT_FOUND));

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

                if (tour.getCruise() == null) {
                        throw new AppException(
                                        "Tour has not been assigned to a cruise",
                                        HttpStatus.BAD_REQUEST);
                }

                if (!tour.getCruise().getId()
                                .equals(cruiseArea.getCruiseDeck().getCruise().getId())) {

                        throw new AppException(
                                        "Cruise area does not belong to the cruise assigned to this tour",
                                        HttpStatus.BAD_REQUEST);
                }

                // Chống trùng lặp bản ghi
                return assignmentRepository.findByTourIdAndCruiseAreaId(request.tourId(), request.cruiseAreaId())
                                .map(ActivityCruiseTourAssignmentMapper::toResponse)
                                .orElseGet(() -> {
                                        ActivityCruiseTour assignment = new ActivityCruiseTour();
                                        assignment.setTour(tour);
                                        assignment.setCruiseArea(cruiseArea);
                                        assignment.setActivityCruise(null);
                                        assignment.setStartTime(null);
                                        assignment.setEndTime(null);
                                        assignment.setMaxPassengers(null);
                                        assignment.setPrice(null);
                                        assignment.setStatus(ActivityCruiseTourStatus.WAITING_CONFIG);

                                        ActivityCruiseTour saved = assignmentRepository.save(assignment);
                                        return ActivityCruiseTourAssignmentMapper.toResponse(saved);
                                });
        }

        /**
         * Lấy toàn bộ phân công của một Tour.
         */
        @Transactional(readOnly = true)
        public List<ActivityCruiseTourAssignmentResponse> getByTour(
                        UUID tourId) {

                if (!tourRepository.existsById(tourId)) {
                        throw new AppException(
                                        "Tour not found",
                                        HttpStatus.NOT_FOUND);
                }

                return assignmentRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                                .stream()
                                .map(ActivityCruiseTourAssignmentMapper::toResponse)
                                .toList();
        }

        /**
         * Xóa phân công khi chưa được Onboard cấu hình.
         */
        @Transactional
        public void deleteByTourAndArea(UUID tourId, UUID cruiseAreaId) {
                ActivityCruiseTour assignment = assignmentRepository
                                .findByTourIdAndCruiseAreaId(tourId, cruiseAreaId)
                                .orElseThrow(() -> new AppException("Assignment not found", HttpStatus.NOT_FOUND));

                if (assignment.getStatus() != ActivityCruiseTourStatus.WAITING_CONFIG) {
                        throw new AppException(
                                        "Cannot delete an assignment that has already been configured",
                                        HttpStatus.BAD_REQUEST);
                }

                assignmentRepository.delete(assignment);
        }
}