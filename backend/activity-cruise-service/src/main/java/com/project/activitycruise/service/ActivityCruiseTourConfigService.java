package com.project.activitycruise.service;

import com.project.activitycruise.dto.onboard.ActivityCruiseTourConfigRequest;
import com.project.activitycruise.model.ActivityCruise;
import com.project.activitycruise.model.ActivityCruiseTour;
import com.project.activitycruise.model.enums.ActivityCruiseStatus;
import com.project.activitycruise.model.enums.ActivityCruiseTourStatus;
import com.project.tour.dto.tour.operation.ActivityCruiseTourAssignmentResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.ActivityCruiseTourAssignmentMapper;
import com.project.tour.repository.onboard.ActivityCruiseRepository;
import com.project.tour.repository.tour.ActivityCruiseTourAssignmentRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ActivityCruiseTourConfigService {

        private final ActivityCruiseTourAssignmentRepository assignmentRepository;
        private final ActivityCruiseRepository activityCruiseRepository;

        public ActivityCruiseTourConfigService(
                        ActivityCruiseTourAssignmentRepository assignmentRepository,
                        ActivityCruiseRepository activityCruiseRepository) {

                this.assignmentRepository = assignmentRepository;
                this.activityCruiseRepository = activityCruiseRepository;
        }

        // =====================================================
        // CREATE CONFIGURATION
        // POST
        // =====================================================

        /**
         * Onboard cấu hình lần đầu cho assignment.
         *
         * Chỉ được cấu hình khi assignment đang:
         *
         * WAITING_CONFIG
         *
         * Sau khi cấu hình thành công:
         *
         * WAITING_CONFIG -> NOT_STARTED
         */
        public ActivityCruiseTourAssignmentResponse configure(
                        UUID assignmentId,
                        ActivityCruiseTourConfigRequest request) {

                ActivityCruiseTour assignment = assignmentRepository
                                .findById(assignmentId)
                                .orElseThrow(() -> new AppException(
                                                "Activity cruise tour assignment not found",
                                                HttpStatus.NOT_FOUND));

                // -------------------------------------------------
                // Chỉ cho cấu hình assignment đang WAITING_CONFIG
                // -------------------------------------------------

                if (assignment.getStatus() != ActivityCruiseTourStatus.WAITING_CONFIG) {
                        throw new AppException(
                                        "Activity cruise tour is not waiting for configuration",
                                        HttpStatus.BAD_REQUEST);
                }

                // -------------------------------------------------
                // Lấy ActivityCruise master
                // -------------------------------------------------

                ActivityCruise activityCruise = activityCruiseRepository
                                .findById(request.activityCruiseId())
                                .orElseThrow(() -> new AppException(
                                                "Activity cruise not found",
                                                HttpStatus.NOT_FOUND));

                // -------------------------------------------------
                // Kiểm tra ActivityCruise có đang hoạt động không
                // -------------------------------------------------

                if (activityCruise.getStatus() != ActivityCruiseStatus.ACTIVE) {
                        throw new AppException(
                                        "Activity cruise is not active",
                                        HttpStatus.BAD_REQUEST);
                }

                // -------------------------------------------------
                // Validate thời gian
                // -------------------------------------------------

                validateTime(
                                request.startTime(),
                                request.endTime());

                // -------------------------------------------------
                // Apply configuration
                // -------------------------------------------------

                ActivityCruiseTourAssignmentMapper.applyConfig(
                                assignment,
                                request,
                                activityCruise);

                // -------------------------------------------------
                // Cấu hình lần đầu hoàn tất
                // -------------------------------------------------

                assignment.setStatus(ActivityCruiseTourStatus.NOT_STARTED);

                ActivityCruiseTour saved = assignmentRepository.save(assignment);

                return ActivityCruiseTourAssignmentMapper.toResponse(saved);
        }

        // =====================================================
        // UPDATE CONFIGURATION
        // PATCH
        // =====================================================

        /**
         * Onboard cập nhật lại cấu hình.
         *
         * Chỉ cho phép cập nhật khi assignment đã:
         *
         * NOT_STARTED
         *
         * Không cho PATCH assignment đang WAITING_CONFIG
         * vì WAITING_CONFIG phải dùng POST để cấu hình lần đầu.
         */
        public ActivityCruiseTourAssignmentResponse updateConfig(
                        UUID assignmentId,
                        ActivityCruiseTourConfigRequest request) {

                ActivityCruiseTour assignment = assignmentRepository
                                .findById(assignmentId)
                                .orElseThrow(() -> new AppException(
                                                "Activity cruise tour assignment not found",
                                                HttpStatus.NOT_FOUND));

                // -------------------------------------------------
                // Chỉ cho cập nhật assignment đã cấu hình
                // -------------------------------------------------

                if (assignment.getStatus() != ActivityCruiseTourStatus.NOT_STARTED) {
                        throw new AppException(
                                        "Only configured activities can be updated",
                                        HttpStatus.BAD_REQUEST);
                }

                // -------------------------------------------------
                // Lấy ActivityCruise master
                // -------------------------------------------------

                ActivityCruise activityCruise = activityCruiseRepository
                                .findById(request.activityCruiseId())
                                .orElseThrow(() -> new AppException(
                                                "Activity cruise not found",
                                                HttpStatus.NOT_FOUND));

                // -------------------------------------------------
                // ActivityCruise phải đang ACTIVE
                // -------------------------------------------------

                if (activityCruise.getStatus() != ActivityCruiseStatus.ACTIVE) {
                        throw new AppException(
                                        "Activity cruise is not active",
                                        HttpStatus.BAD_REQUEST);
                }

                // -------------------------------------------------
                // Validate thời gian
                // -------------------------------------------------

                validateTime(
                                request.startTime(),
                                request.endTime());

                // -------------------------------------------------
                // Apply configuration mới
                // -------------------------------------------------

                ActivityCruiseTourAssignmentMapper.applyConfig(
                                assignment,
                                request,
                                activityCruise);

                // -------------------------------------------------
                // Giữ nguyên NOT_STARTED
                // -------------------------------------------------

                assignment.setStatus(ActivityCruiseTourStatus.NOT_STARTED);

                ActivityCruiseTour saved = assignmentRepository.save(assignment);

                return ActivityCruiseTourAssignmentMapper.toResponse(saved);
        }

        // =====================================================
        // VALIDATION
        // =====================================================

        private void validateTime(
                        java.time.LocalDateTime startTime,
                        java.time.LocalDateTime endTime) {

                if (!startTime.isBefore(endTime)) {
                        throw new AppException(
                                        "Start time must be before end time",
                                        HttpStatus.BAD_REQUEST);
                }
        }
}