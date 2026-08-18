package com.project.tour.mapper.tour.schedule;

import com.project.tour.dto.tour.schedule.CreateScheduleRequest;
import com.project.tour.dto.tour.schedule.ScheduleResponse;
import com.project.tour.dto.tour.schedule.UpdateScheduleRequest;
import com.project.tour.model.Schedule;
import com.project.tour.model.Tour;

public class ScheduleMapper {

    public static Schedule toEntity(
            CreateScheduleRequest request,
            Tour tour) {

        Schedule schedule = new Schedule();

        schedule.setTour(tour);
        schedule.setName(request.getName());
        schedule.setDescription(request.getDescription());
        schedule.setDayNumber(request.getDayNumber());
        schedule.setRealDay(request.getRealDay());

        return schedule;
    }

    public static void updateEntity(
            Schedule schedule,
            UpdateScheduleRequest request) {

        schedule.setName(request.getName());
        schedule.setDescription(request.getDescription());
        schedule.setDayNumber(request.getDayNumber());
        schedule.setRealDay(request.getRealDay());
        schedule.setStatus(request.getStatus());
    }

    public static ScheduleResponse toResponse(
            Schedule schedule) {

        ScheduleResponse response = new ScheduleResponse();

        response.setId(schedule.getId());
        response.setTourId(schedule.getTour().getId());
        response.setName(schedule.getName());
        response.setDescription(schedule.getDescription());
        response.setDayNumber(schedule.getDayNumber());
        response.setRealDay(schedule.getRealDay());
        response.setStatus(schedule.getStatus());

        return response;
    }
}