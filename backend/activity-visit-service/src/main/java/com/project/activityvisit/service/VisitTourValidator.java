package com.project.activityvisit.service;

import com.project.activityvisit.dto.CreateVisitTourRequest;
import com.project.activityvisit.dto.UpdateVisitTourRequest;
import com.project.activityvisit.exception.AppException;
import com.project.activityvisit.model.enums.VisitTourStatus;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class VisitTourValidator {

        // =====================================================
        // CREATE
        // =====================================================

        public void validateCreate(
                        CreateVisitTourRequest request) {

                if (request == null) {
                        throw badRequest(
                                        "Request must not be null");
                }

                validateName(request.name());

                validateTime(
                                request.startTime(),
                                request.endTime());

                validateMaxPassengers(
                                request.maxPassengers());

                validatePrice(
                                request.price());
        }

        // =====================================================
        // UPDATE
        // =====================================================

        public void validateUpdate(
                        UpdateVisitTourRequest request) {

                if (request == null) {
                        throw badRequest(
                                        "Request must not be null");
                }

                if (request.name() != null) {
                        validateName(request.name());
                }

                if (request.maxPassengers() != null) {
                        validateMaxPassengers(
                                        request.maxPassengers());
                }

                if (request.price() != null) {
                        validatePrice(
                                        request.price());
                }

                // Nếu PATCH truyền cả hai thì kiểm tra lại khoảng thời gian
                if (request.startTime() != null
                                && request.endTime() != null) {

                        validateTime(
                                        request.startTime(),
                                        request.endTime());
                }
        }

        // =====================================================
        // TIME
        // =====================================================

        public void validateTime(
                        LocalDateTime startTime,
                        LocalDateTime endTime) {

                if (startTime == null
                                || endTime == null) {

                        throw badRequest(
                                        "Start time and end time are required");
                }

                if (!startTime.isBefore(endTime)) {

                        throw badRequest(
                                        "Start time must be before end time");
                }
        }

        // =====================================================
        // STATUS
        // =====================================================

        public void validateStatusTransition(
                        VisitTourStatus currentStatus,
                        VisitTourStatus newStatus) {

                if (currentStatus == null) {
                        throw badRequest(
                                        "Current visit tour status is required");
                }

                if (newStatus == null) {
                        throw badRequest(
                                        "New visit tour status is required");
                }

                if (currentStatus == newStatus) {
                        return;
                }

                boolean allowed = switch (currentStatus) {

                        case WAITING_CONFIG ->
                                newStatus == VisitTourStatus.CONFIGURED
                                                || newStatus == VisitTourStatus.CANCELLED;

                        case CONFIGURED ->
                                newStatus == VisitTourStatus.NOT_STARTED
                                                || newStatus == VisitTourStatus.CANCELLED;

                        case NOT_STARTED ->
                                newStatus == VisitTourStatus.IN_PROGRESS
                                                || newStatus == VisitTourStatus.CANCELLED;

                        case IN_PROGRESS ->
                                newStatus == VisitTourStatus.COMPLETED
                                                || newStatus == VisitTourStatus.DELAYED
                                                || newStatus == VisitTourStatus.CANCELLED;

                        case DELAYED ->
                                newStatus == VisitTourStatus.IN_PROGRESS
                                                || newStatus == VisitTourStatus.COMPLETED
                                                || newStatus == VisitTourStatus.CANCELLED;

                        case COMPLETED,
                                        CANCELLED ->
                                false;
                };

                if (!allowed) {
                        throw badRequest(
                                        "Invalid VisitTour status transition: "
                                                        + currentStatus
                                                        + " -> "
                                                        + newStatus);
                }
        }

        // =====================================================
        // NAME
        // =====================================================

        private void validateName(
                        String name) {

                if (name == null
                                || name.isBlank()) {

                        throw badRequest(
                                        "Visit tour name is required");
                }
        }

        // =====================================================
        // MAX PASSENGERS
        // =====================================================

        private void validateMaxPassengers(
                        Integer maxPassengers) {

                if (maxPassengers == null
                                || maxPassengers <= 0) {

                        throw badRequest(
                                        "Max passengers must be greater than zero");
                }
        }

        // =====================================================
        // PRICE
        // =====================================================

        private void validatePrice(
                        BigDecimal price) {

                if (price == null
                                || price.signum() < 0) {

                        throw badRequest(
                                        "Price must be greater than or equal to zero");
                }
        }

        // =====================================================
        // EXCEPTION
        // =====================================================

        private AppException badRequest(
                        String message) {

                return new AppException(
                                message,
                                HttpStatus.BAD_REQUEST);
        }

        public void validateTimeRange(
                        LocalDateTime arriveAt,
                        LocalDateTime leaveAt,
                        LocalDateTime startTime,
                        LocalDateTime endTime) {

                if (arriveAt == null || leaveAt == null) {
                        throw badRequest(
                                        "Ship arrival and departure time are required");
                }

                validateTime(startTime, endTime);

                if (startTime.isBefore(arriveAt)) {
                        throw badRequest(
                                        "Visit tour cannot start before the ship arrives at the port");
                }

                if (endTime.isAfter(leaveAt)) {
                        throw badRequest(
                                        "Visit tour cannot end after the ship leaves the port");
                }
        }
}