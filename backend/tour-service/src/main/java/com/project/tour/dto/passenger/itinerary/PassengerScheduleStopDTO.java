package com.project.tour.dto.passenger.itinerary;

import com.project.tour.dto.passenger.route.PassengerPortDTO;

import java.time.LocalDateTime;
import java.util.UUID;

public record PassengerScheduleStopDTO(
        UUID id,
        Integer stopOrder,
        PassengerPortDTO port,
        LocalDateTime arriveAt,
        LocalDateTime leaveAt) {
}