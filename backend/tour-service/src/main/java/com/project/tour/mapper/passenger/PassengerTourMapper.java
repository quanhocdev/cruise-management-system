package com.project.tour.mapper.passenger;

import com.project.tour.dto.guest.PortInfoRecord;
import com.project.tour.dto.passenger.PassengerTourDetailResponse;
import com.project.tour.model.admin.Cruise;
import com.project.tour.model.admin.Port;
import com.project.tour.model.scheduler.Schedule;
import com.project.tour.model.scheduler.ScheduleStop;
import com.project.tour.model.scheduler.Tour;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PassengerTourMapper {

    public static PassengerTourDetailResponse toDetailResponse(
            Tour tour,
            List<Schedule> schedules,
            Map<UUID, List<ScheduleStop>> scheduleIdToStopsMap) {

        Cruise cruise = tour.getCruise();

        Port dep = tour.getDeparturePort();
        PortInfoRecord depRecord = dep != null ? new PortInfoRecord(
                dep.getId(),
                dep.getName(),
                null, // Nếu Port chưa có code, truyền tạm null hoặc ""
                dep.getCity(),
                dep.getCountry(),
                dep.getAddress()) : null;

        Port arr = tour.getArrivalPort();
        PortInfoRecord arrRecord = arr != null ? new PortInfoRecord(
                arr.getId(),
                arr.getName(),
                null,
                arr.getCity(),
                arr.getCountry(),
                arr.getAddress()) : null;

        PassengerTourDetailResponse.CruiseDetailRecord cruiseRecord = new PassengerTourDetailResponse.CruiseDetailRecord(
                cruise.getId(), cruise.getName(), cruise.getCode(), cruise.getDescription(), cruise.getMaxPassengers(),
                cruise.getImageUrl());

        List<PassengerTourDetailResponse.ScheduleDetailRecord> scheduleRecords = schedules.stream().map(schedule -> {
            List<ScheduleStop> stops = scheduleIdToStopsMap.getOrDefault(schedule.getId(), List.of());
            List<PassengerTourDetailResponse.ScheduleStopRecord> stopRecords = stops.stream().map(stop -> {
                Port port = stop.getPort();
                return new PassengerTourDetailResponse.ScheduleStopRecord(
                        stop.getId(),
                        stop.getStopOrder(),
                        stop.getArriveAt(),
                        stop.getLeaveAt(),
                        port != null ? port.getName() : null,
                        port != null ? port.getCity() : null,
                        port != null ? port.getCountry() : null,
                        port != null ? port.getDescription() : null);
            }).toList();

            return new PassengerTourDetailResponse.ScheduleDetailRecord(
                    schedule.getId(),
                    schedule.getName(),
                    schedule.getDescription(),
                    schedule.getDayNumber(),
                    schedule.getRealDay(),
                    stopRecords);
        }).toList();

        return new PassengerTourDetailResponse(
                tour.getId(),
                tour.getCode(),
                tour.getName(),
                tour.getDescription(),
                tour.getStartDate(),
                tour.getEndDate(),
                tour.getStatusBooking(),
                tour.getBookingStart(),
                tour.getBookingEnd(),
                depRecord,
                arrRecord,
                cruiseRecord,
                scheduleRecords);
    }
}