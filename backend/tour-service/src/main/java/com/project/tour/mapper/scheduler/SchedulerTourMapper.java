package com.project.tour.mapper.scheduler;

import com.project.tour.dto.scheduler.SchedulerTourDtos.*;
import com.project.tour.model.admin.Port;
import com.project.tour.model.scheduler.Schedule;
import com.project.tour.model.scheduler.ScheduleStop;
import com.project.tour.model.scheduler.Tour;
import com.project.tour.model.scheduler.TourRoute;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SchedulerTourMapper {

    public TourSummaryResponse toSummaryResponse(Tour tour) {
        if (tour == null)
            return null;
        return new TourSummaryResponse(
                tour.getId(),
                tour.getCode(),
                tour.getName(),
                tour.getStatusTrip(),
                tour.getStartDate(),
                tour.getEndDate());
    }

    public TourDetailResponse toDetailResponse(
            Tour tour,
            TourRoute route,
            List<Schedule> schedules,
            List<List<ScheduleStop>> stopsPerSchedule,
            VesselPositionInfo currentPosition) {

        if (tour == null)
            return null;

        PortInfo departurePortInfo = null;
        PortInfo arrivalPortInfo = null;

        if (route != null) {
            Port dep = route.getDeparturePort();
            if (dep != null) {
                departurePortInfo = toPortInfo(dep);
            }
            Port arr = route.getDestinationPort();
            if (arr != null) {
                arrivalPortInfo = toPortInfo(arr);
            }
        }

        List<ScheduleDayInfo> scheduleDayInfos = List.of();
        if (schedules != null && !schedules.isEmpty()) {
            scheduleDayInfos = new java.util.ArrayList<>();
            for (int i = 0; i < schedules.size(); i++) {
                Schedule schedule = schedules.get(i);
                List<ScheduleStop> stops = (stopsPerSchedule != null && i < stopsPerSchedule.size())
                        ? stopsPerSchedule.get(i)
                        : List.of();

                List<StopInfo> stopInfos = stops.stream()
                        .map(stop -> new StopInfo(
                                stop.getId(),
                                stop.getStopOrder(),
                                toPortInfo(stop.getPort()),
                                stop.getArriveAt(),
                                stop.getLeaveAt()))
                        .toList();

                scheduleDayInfos.add(new ScheduleDayInfo(
                        schedule.getId(),
                        schedule.getName(),
                        schedule.getDayNumber(),
                        schedule.getRealDay(),
                        stopInfos));
            }
        }

        return new TourDetailResponse(
                tour.getId(),
                tour.getCode(),
                tour.getName(),
                tour.getDescription(),
                tour.getStatusTrip(),
                tour.getStartDate(),
                tour.getEndDate(),
                departurePortInfo,
                arrivalPortInfo,
                scheduleDayInfos,
                currentPosition);
    }

    public PortInfo toPortInfo(Port port) {
        if (port == null)
            return null;
        return new PortInfo(
                port.getId(),
                port.getName(),
                port.getCity(),
                port.getCountry(),
                port.getLatitude(),
                port.getLongitude());
    }
}