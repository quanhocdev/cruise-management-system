package com.project.activitycruise.service;

import com.project.activitycruise.dto.ActivityCruiseTourResponse;
import com.project.activitycruise.dto.ActivityCruiseTourConfigRequest;
import com.project.activitycruise.exception.AppException;
import com.project.activitycruise.mapper.ActivityCruiseTourMapper;
import com.project.activitycruise.model.ActivityCruise;
import com.project.activitycruise.model.ActivityCruiseTour;
import com.project.activitycruise.model.enums.ActivityCruiseStatus;
import com.project.activitycruise.model.enums.ActivityCruiseTourStatus;
import com.project.activitycruise.repository.ActivityCruiseRepository;
import com.project.activitycruise.repository.ActivityCruiseTourAssignmentRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

        public ActivityCruiseTourResponse configure(
                        UUID assignmentId,
                        ActivityCruiseTourConfigRequest request) {

                ActivityCruiseTour assignment = assignmentRepository
                                .findById(assignmentId)
                                .orElseThrow(() -> new AppException(
                                                "Activity cruise tour assignment not found",
                                                HttpStatus.NOT_FOUND));

                if (assignment.getStatus() != ActivityCruiseTourStatus.WAITING_CONFIG) {
                        throw new AppException(
                                        "Activity cruise tour is not waiting for configuration",
                                        HttpStatus.BAD_REQUEST);
                }

                ActivityCruise activityCruise = activityCruiseRepository
                                .findById(request.activityCruiseId())
                                .orElseThrow(() -> new AppException(
                                                "Activity cruise not found",
                                                HttpStatus.NOT_FOUND));

                if (activityCruise.getStatus() != ActivityCruiseStatus.ACTIVE) {
                        throw new AppException(
                                        "Activity cruise is not active",
                                        HttpStatus.BAD_REQUEST);
                }

                validateTime(
                                request.startTime(),
                                request.endTime());

                activityCruiseTourMapper.applyConfig(
                                assignment,
                                request,
                                activityCruise);

                // Đã cấu hình xong nhưng Tour chưa READY
                assignment.setStatus(ActivityCruiseTourStatus.CONFIGURED);

                ActivityCruiseTour saved = assignmentRepository.save(assignment);

                return activityCruiseTourMapper.toResponse(saved);
        }

        public ActivityCruiseTourResponse updateConfig(
                        UUID assignmentId,
                        ActivityCruiseTourConfigRequest request) {

                ActivityCruiseTour assignment = assignmentRepository
                                .findById(assignmentId)
                                .orElseThrow(() -> new AppException(
                                                "Activity cruise tour assignment not found",
                                                HttpStatus.NOT_FOUND));

                if (assignment.getStatus() != ActivityCruiseTourStatus.CONFIGURED) {
                        throw new AppException(
                                        "Only CONFIGURED activities can be updated",
                                        HttpStatus.BAD_REQUEST);
                }

                ActivityCruise activityCruise = activityCruiseRepository
                                .findById(request.activityCruiseId())
                                .orElseThrow(() -> new AppException(
                                                "Activity cruise not found",
                                                HttpStatus.NOT_FOUND));

                if (activityCruise.getStatus() != ActivityCruiseStatus.ACTIVE) {
                        throw new AppException(
                                        "Activity cruise is not active",
                                        HttpStatus.BAD_REQUEST);
                }

                validateTime(
                                request.startTime(),
                                request.endTime());

                activityCruiseTourMapper.applyConfig(
                                assignment,
                                request,
                                activityCruise);

                // PATCH vẫn đang ở trạng thái đã cấu hình
                assignment.setStatus(ActivityCruiseTourStatus.CONFIGURED);

                ActivityCruiseTour saved = assignmentRepository.save(assignment);

                return activityCruiseTourMapper.toResponse(saved);
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