
package com.project.booking.dto.booking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PassengerTourTrackingResponse(
        UUID tourId,
        String code,
        String name,
        String description,
        String statusTrip,
        LocalDate startDate,
        LocalDate endDate,
        PortInfo departurePort,
        PortInfo arrivalPort,
        List<ScheduleDayInfo> schedules,
        VesselPositionInfo currentPosition) {

    public record PortInfo(
            UUID portId,
            String name,
            String city,
            String country,
            BigDecimal latitude,
            BigDecimal longitude) {
    }

    public record ScheduleDayInfo(
            UUID scheduleId,
            String name,
            Integer dayNumber,
            LocalDate realDay,
            List<StopInfo> stops) {
    }

    public record StopInfo(
            UUID stopId,
            Integer stopOrder,
            PortInfo port,
            LocalDateTime arriveAt,
            LocalDateTime leaveAt) {
    }

    public record VesselPositionInfo(
            double latitude,
            double longitude,
            LocalDateTime updatedAt) {
    }
}
