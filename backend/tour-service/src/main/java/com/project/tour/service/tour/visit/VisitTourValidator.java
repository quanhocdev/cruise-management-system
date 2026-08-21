package com.project.tour.service.tour.visit;

import com.project.tour.dto.visit.CreateVisitTourRequest;
import com.project.tour.dto.visit.UpdateVisitTourRequest;
import com.project.tour.exception.AppException;
import com.project.tour.model.ScheduleStop;
import com.project.tour.model.VisitTour;
import com.project.tour.model.enums.visit.VisitTourStatus;
import com.project.tour.model.Tour;
import com.project.tour.model.enums.tour.TourStatusTrip;
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

        if (request.scheduleStopId() == null) {
            throw badRequest(
                    "Schedule stop is required");
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
    }

    // =====================================================
    // TIME RANGE
    // =====================================================

    public void validateTimeRange(
            ScheduleStop scheduleStop,
            LocalDateTime startTime,
            LocalDateTime endTime) {

        if (scheduleStop == null) {
            throw badRequest(
                    "Schedule stop is required");
        }

        validateTime(
                startTime,
                endTime);

        if (startTime.isBefore(
                scheduleStop.getArriveAt())) {

            throw badRequest(
                    "Visit tour cannot start before the ship arrives at the port");
        }

        if (endTime.isAfter(
                scheduleStop.getLeaveAt())) {

            throw badRequest(
                    "Visit tour cannot end after the ship leaves the port");
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
    // TIME
    // =====================================================

    private void validateTime(
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

    // =====================================================
    // TOUR STATUS - MODIFY
    // =====================================================

    public void validateTourCanModify(
            Tour tour) {

        if (tour == null) {
            throw badRequest(
                    "Tour is required");
        }

        TourStatusTrip status = tour.getStatusTrip();

        if (status != TourStatusTrip.APPROVED
                && status != TourStatusTrip.IN_PROGRESS) {

            throw badRequest(
                    "Visit tour cannot be modified when tour status is "
                            + status);
        }
    }
}