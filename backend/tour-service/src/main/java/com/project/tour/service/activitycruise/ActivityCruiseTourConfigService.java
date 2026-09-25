package com.project.tour.service.activitycruise;

import com.project.tour.dto.activitycruise.ActivityCruiseTourConfigRequest;
import com.project.tour.dto.activitycruise.ActivityCruiseTourResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.activitycruise.ActivityCruiseTourMapper;
import com.project.tour.model.activitycruise.ActivityCruise;
import com.project.tour.model.activitycruise.ActivityCruiseTour;
import com.project.tour.model.activitycruise.enums.ActivityCruiseStatus;
import com.project.tour.model.activitycruise.enums.ActivityCruiseTourStatus;
import com.project.tour.repository.activitycruise.ActivityCruiseRepository;
import com.project.tour.repository.activitycruise.ActivityCruiseTourAssignmentRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ActivityCruiseTourConfigService {

        private final ActivityCruiseTourAssignmentRepository assignmentRepository;
        private final ActivityCruiseRepository activityCruiseRepository;
        private final ActivityCruiseTourMapper activityCruiseTourMapper;

        public ActivityCruiseTourConfigService(
                        ActivityCruiseTourAssignmentRepository assignmentRepository,
                        ActivityCruiseRepository activityCruiseRepository,
                        ActivityCruiseTourMapper activityCruiseTourMapper) {

                this.assignmentRepository = assignmentRepository;
                this.activityCruiseRepository = activityCruiseRepository;
                this.activityCruiseTourMapper = activityCruiseTourMapper;
        }

        // =====================================================
        // SAVE CONFIGURATION
        // =====================================================

        public ActivityCruiseTourResponse configure(
                        UUID assignmentId,
                        ActivityCruiseTourConfigRequest request) {

                ActivityCruiseTour assignment = findAssignment(assignmentId);

                ensureWaitingConfig(assignment);

                ActivityCruise activityCruise = findActiveActivityCruise(
                                request.activityCruiseId());

                validateTime(
                                request.startTime(),
                                request.endTime());

                activityCruiseTourMapper.applyConfig(
                                assignment,
                                request,
                                activityCruise);

                // =================================================
                // KHÔNG ĐỔI STATUS
                // WAITING_CONFIG → WAITING_CONFIG
                // =================================================

                return activityCruiseTourMapper.toResponse(
                                assignmentRepository.save(assignment));
        }

        // =====================================================
        // UPDATE CONFIGURATION
        // =====================================================

        public ActivityCruiseTourResponse updateConfig(
                        UUID assignmentId,
                        ActivityCruiseTourConfigRequest request) {

                ActivityCruiseTour assignment = findAssignment(assignmentId);

                ensureWaitingConfig(assignment);

                ActivityCruise activityCruise = findActiveActivityCruise(
                                request.activityCruiseId());

                validateTime(
                                request.startTime(),
                                request.endTime());

                activityCruiseTourMapper.applyConfig(
                                assignment,
                                request,
                                activityCruise);

                // =================================================
                // KHÔNG ĐỔI STATUS
                // WAITING_CONFIG → WAITING_CONFIG
                // =================================================

                return activityCruiseTourMapper.toResponse(
                                assignmentRepository.save(assignment));
        }

        // =====================================================
        // COMPLETE CONFIGURATION
        // =====================================================

        public void complete(UUID tourId) {

                List<ActivityCruiseTour> assignments = assignmentRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId);

                if (assignments.isEmpty()) {
                        throw new AppException(
                                        "No activity cruise configuration found for tour",
                                        HttpStatus.NOT_FOUND);
                }

                // =================================================
                // ĐÃ COMPLETE TRƯỚC ĐÓ
                // =================================================

                boolean alreadyConfigured = assignments.stream()
                                .allMatch(assignment -> assignment.getStatus() == ActivityCruiseTourStatus.CONFIGURED);

                if (alreadyConfigured) {
                        throw new AppException(
                                        "Activity cruise configuration for this tour has already been completed",
                                        HttpStatus.CONFLICT);
                }

                // =================================================
                // TẤT CẢ PHẢI ĐANG WAITING_CONFIG
                // =================================================

                for (ActivityCruiseTour assignment : assignments) {

                        if (assignment.getStatus() != ActivityCruiseTourStatus.WAITING_CONFIG) {

                                throw new AppException(
                                                "All activity cruise tours must be WAITING_CONFIG before completing configuration",
                                                HttpStatus.BAD_REQUEST);
                        }
                }

                // =================================================
                // KIỂM TRA CẤU HÌNH ĐẦY ĐỦ
                // =================================================

                for (ActivityCruiseTour assignment : assignments) {

                        if (assignment.getActivityCruise() == null) {
                                throw new AppException(
                                                "All activity cruise tours must have an activity selected before completing configuration",
                                                HttpStatus.BAD_REQUEST);
                        }

                        if (assignment.getActivityName() == null
                                        || assignment.getActivityName().isBlank()
                                        || assignment.getStartTime() == null
                                        || assignment.getEndTime() == null
                                        || assignment.getMaxPassengers() == null
                                        || assignment.getPrice() == null) {

                                throw new AppException(
                                                "All activity cruise tours must be fully configured before completing configuration",
                                                HttpStatus.BAD_REQUEST);
                        }

                        if (!assignment.getStartTime()
                                        .isBefore(assignment.getEndTime())) {

                                throw new AppException(
                                                "Activity cruise start time must be before end time",
                                                HttpStatus.BAD_REQUEST);
                        }

                        if (assignment.getMaxPassengers() <= 0) {
                                throw new AppException(
                                                "Maximum passengers must be greater than zero",
                                                HttpStatus.BAD_REQUEST);
                        }

                        if (assignment.getPrice()
                                        .compareTo(BigDecimal.ZERO) < 0) {

                                throw new AppException(
                                                "Price must not be negative",
                                                HttpStatus.BAD_REQUEST);
                        }
                }

                // =================================================
                // COMPLETE CONFIGURATION
                // WAITING_CONFIG → CONFIGURED
                // =================================================

                for (ActivityCruiseTour assignment : assignments) {

                        assignment.setStatus(
                                        ActivityCruiseTourStatus.CONFIGURED);
                }

                assignmentRepository.saveAll(assignments);
        }

        // =====================================================
        // FIND ASSIGNMENT
        // =====================================================

        private ActivityCruiseTour findAssignment(UUID assignmentId) {

                return assignmentRepository
                                .findById(assignmentId)
                                .orElseThrow(() -> new AppException(
                                                "Activity cruise tour assignment not found",
                                                HttpStatus.NOT_FOUND));
        }

        // =====================================================
        // CHECK WAITING CONFIG
        // =====================================================

        private void ensureWaitingConfig(
                        ActivityCruiseTour assignment) {

                if (assignment.getStatus() != ActivityCruiseTourStatus.WAITING_CONFIG) {

                        throw new AppException(
                                        "Activity cruise configuration has already been completed and cannot be modified",
                                        HttpStatus.CONFLICT);
                }
        }

        // =====================================================
        // FIND ACTIVE ACTIVITY CRUISE
        // =====================================================

        private ActivityCruise findActiveActivityCruise(
                        UUID activityCruiseId) {

                ActivityCruise activityCruise = activityCruiseRepository
                                .findById(activityCruiseId)
                                .orElseThrow(() -> new AppException(
                                                "Activity cruise not found",
                                                HttpStatus.NOT_FOUND));

                if (activityCruise.getStatus() != ActivityCruiseStatus.ACTIVE) {

                        throw new AppException(
                                        "Activity cruise is not active",
                                        HttpStatus.BAD_REQUEST);
                }

                return activityCruise;
        }

        // =====================================================
        // VALIDATION
        // =====================================================

        private void validateTime(
                        LocalDateTime startTime,
                        LocalDateTime endTime) {

                if (startTime == null || endTime == null) {

                        throw new AppException(
                                        "Start time and end time are required",
                                        HttpStatus.BAD_REQUEST);
                }

                if (!startTime.isBefore(endTime)) {

                        throw new AppException(
                                        "Start time must be before end time",
                                        HttpStatus.BAD_REQUEST);
                }
        }
}
