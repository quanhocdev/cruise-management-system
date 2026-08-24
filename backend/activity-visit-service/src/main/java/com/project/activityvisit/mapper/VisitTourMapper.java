// src/main/java/com/project/tour/mapper/tour/VisitTourMapper.java

package com.project.tour.mapper.tour;

import com.project.activityvisit.dto.CreateVisitTourRequest;
import com.project.activityvisit.dto.UpdateVisitTourRequest;
import com.project.activityvisit.dto.VisitTourResponse;
import com.project.tour.model.Schedule;
import com.project.tour.model.ScheduleStop;
import com.project.tour.model.Tour;
import com.project.tour.model.VisitTour;

public final class VisitTourMapper {

    private VisitTourMapper() {
    }

    // =====================================================
    // CREATE REQUEST -> ENTITY
    // =====================================================

    public static VisitTour toEntity(
            CreateVisitTourRequest request,
            ScheduleStop scheduleStop) {

        VisitTour visitTour = new VisitTour();

        visitTour.setScheduleStop(scheduleStop);
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
            visitTour.setName(request.name());
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

    public static VisitTourResponse toResponse(VisitTour visitTour) {

        ScheduleStop stop = visitTour.getScheduleStop();
        Schedule schedule = stop.getSchedule();
        Tour tour = schedule.getTour();

        return new VisitTourResponse(
                visitTour.getId(),

                // TOUR
                tour.getId(),
                tour.getCode(),
                tour.getName(),

                // SCHEDULE
                schedule.getId(),
                schedule.getDayNumber(),

                // SCHEDULE STOP
                stop.getId(),
                stop.getStopOrder(),

                // PORT
                stop.getPort().getId(),
                stop.getPort().getName(),

                // SHIP ARRIVAL / DEPARTURE
                stop.getArriveAt(),
                stop.getLeaveAt(),

                // VISIT TOUR
                visitTour.getName(),
                visitTour.getDescription(),
                visitTour.getStartTime(),
                visitTour.getEndTime(),
                visitTour.getMaxPassengers(),
                visitTour.getPrice(),
                visitTour.getStatus(),

                // AUDIT
                visitTour.getCreatedAt(),
                visitTour.getUpdatedAt());
    }
}