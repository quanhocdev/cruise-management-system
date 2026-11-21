package com.project.tour.dto.passenger.itinerary;

import com.project.tour.dto.passenger.itinerary.PassengerScheduleStopDTO;
import com.project.tour.model.enums.ScheduleStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PassengerScheduleDTO(
        UUID id,
        String name,
        String description,
        Integer dayNumber,
        LocalDate realDay,
        ScheduleStatus status,
        List<PassengerScheduleStopDTO> stops) {
}