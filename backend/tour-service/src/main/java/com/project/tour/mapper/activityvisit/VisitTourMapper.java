package com.project.tour.mapper.activityvisit;

import com.project.tour.dto.activityvisit.CreateVisitTourRequest;
import com.project.tour.dto.activityvisit.UpdateVisitTourRequest;
import com.project.tour.dto.activityvisit.VisitTourResponse;
import com.project.tour.model.activityvisit.VisitTour;

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
                // REFERENCES
                // =================================================

                visitTour.getTourId(),
                visitTour.getScheduleStopId(),

                // =================================================
                // VISIT TOUR CONFIGURATION
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

