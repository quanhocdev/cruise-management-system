package com.project.tour.mapper.passenger;

import com.project.tour.dto.passenger.itinerary.PassengerScheduleDTO;
import com.project.tour.dto.passenger.itinerary.PassengerScheduleStopDTO;
import com.project.tour.model.scheduler.Schedule;
import com.project.tour.model.scheduler.ScheduleStop;

import java.util.Comparator;
import java.util.List;

public class ScheduleMapper {

    private final PortMapper portMapper;

    public ScheduleMapper(PortMapper portMapper) {
        this.portMapper = portMapper;
    }

    public PassengerScheduleStopDTO toStopDTO(ScheduleStop stop) {
        if (stop == null) {
            return null;
        }

        return new PassengerScheduleStopDTO(
                stop.getId(),
                stop.getStopOrder(),
                portMapper.toDTO(stop.getPort()),
                stop.getArriveAt(),
                stop.getLeaveAt());
    }

    public PassengerScheduleDTO toDTO(
            Schedule schedule,
            List<ScheduleStop> stops) {
        if (schedule == null) {
            return null;
        }

        List<PassengerScheduleStopDTO> stopDTOs = stops == null
                ? List.of()
                : stops.stream()
                        .sorted(Comparator.comparing(ScheduleStop::getStopOrder))
                        .map(this::toStopDTO)
                        .toList();

        return new PassengerScheduleDTO(
                schedule.getId(),
                schedule.getName(),
                schedule.getDescription(),
                schedule.getDayNumber(),
                schedule.getRealDay(),
                schedule.getStatus(),
                stopDTOs);
    }
}