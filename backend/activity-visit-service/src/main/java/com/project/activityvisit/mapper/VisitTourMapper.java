package com.project.activityvisit.mapper;

import com.project.activityvisit.dto.CreateVisitTourRequest;
import com.project.activityvisit.dto.UpdateVisitTourRequest;
import com.project.activityvisit.dto.VisitTourResponse;
import com.project.activityvisit.model.VisitTour;

public final class VisitTourMapper {

    private VisitTourMapper() {
    }

    // =====================================================
    // CREATE REQUEST -> ENTITY
    // =====================================================

    public static VisitTour toEntity(
            CreateVisitTourRequest request) {

        VisitTour visitTour = new VisitTour();

        visitTour.setName(request.name());
        visitTour.setDescription(request.description());
        visitTour.setStartTime(request.startTime());
        visitTour.setEndTime(request.endTime());
        visitTour.setMaxPassengers(request.maxPassengers());
        visitTour.setPrice(request.price());

        return visitTour;
    }

    // =====================================================
    // PATCH REQUEST -> ENTITY
    // =====================================================

    public static void updateEntity(
            VisitTour visitTour,
            UpdateVisitTourRequest request) {

        if (request.name() != null) {
            visitTour.setName(request.name().trim());
        }

        if (request.description() != null) {
            visitTour.setDescription(request.description());
        }

        if (request.startTime() != null) {
            visitTour.setStartTime(request.startTime());
        }

        if (request.endTime() != null) {
            visitTour.setEndTime(request.endTime());
        }

        if (request.maxPassengers() != null) {
            visitTour.setMaxPassengers(request.maxPassengers());
        }

        if (request.price() != null) {
            visitTour.setPrice(request.price());
        }

        if (request.status() != null) {
            visitTour.setStatus(request.status());
        }
    }

    // =====================================================
    // ENTITY -> RESPONSE
    // =====================================================

    public static VisitTourResponse toResponse(
            VisitTour visitTour) {

        return new VisitTourResponse(

                visitTour.getId(),

                // =================================================
                // TOUR
                // =================================================

                visitTour.getTourId(),
                visitTour.getTourCode(),
                visitTour.getTourName(),

                // =================================================
                // SCHEDULE
                // =================================================

                visitTour.getScheduleId(),
                visitTour.getDayNumber(),

                // =================================================
                // SCHEDULE STOP
                // =================================================

                visitTour.getScheduleStopId(),
                visitTour.getStopOrder(),

                // =================================================
                // PORT
                // =================================================

                visitTour.getPortId(),
                visitTour.getPortName(),

                // =================================================
                // SHIP ARRIVAL / DEPARTURE
                // =================================================

                visitTour.getArriveAt(),
                visitTour.getLeaveAt(),

                // =================================================
                // VISIT TOUR
                // =================================================

                visitTour.getName(),
                visitTour.getDescription(),
                visitTour.getStartTime(),
                visitTour.getEndTime(),
                visitTour.getMaxPassengers(),
                visitTour.getPrice(),
                visitTour.getStatus(),

                // =================================================
                // AUDIT
                // =================================================

                visitTour.getCreatedAt(),
                visitTour.getUpdatedAt());
    }
}