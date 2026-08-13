package com.project.tour.mapper.tour;

import com.project.tour.dto.tour.schedule.stop.CreateScheduleStopRequest;
import com.project.tour.dto.tour.schedule.stop.ScheduleStopResponse;
import com.project.tour.dto.tour.schedule.stop.UpdateScheduleStopRequest;
import com.project.tour.model.Port;
import com.project.tour.model.Schedule;
import com.project.tour.model.ScheduleStop;

public class ScheduleStopMapper {

    public static ScheduleStop toEntity(
            CreateScheduleStopRequest request,
            Schedule schedule,
            Port port) {

        ScheduleStop stop = new ScheduleStop();

        stop.setSchedule(schedule);
        stop.setPort(port);
        stop.setStopOrder(request.getStopOrder());
        stop.setArriveAt(request.getArriveAt());
        stop.setLeaveAt(request.getLeaveAt());

        return stop;
    }

    public static void updateEntity(
            ScheduleStop stop,
            UpdateScheduleStopRequest request,
            Port port) {

        stop.setPort(port);
        stop.setStopOrder(request.getStopOrder());
        stop.setArriveAt(request.getArriveAt());
        stop.setLeaveAt(request.getLeaveAt());
    }

    public static ScheduleStopResponse toResponse(
            ScheduleStop stop) {

        ScheduleStopResponse response = new ScheduleStopResponse();

        response.setId(stop.getId());
        response.setScheduleId(
                stop.getSchedule().getId());
        response.setPortId(
                stop.getPort().getId());
        response.setPortName(
                stop.getPort().getName());
        response.setStopOrder(
                stop.getStopOrder());
        response.setArriveAt(
                stop.getArriveAt());
        response.setLeaveAt(
                stop.getLeaveAt());

        return response;
    }
}