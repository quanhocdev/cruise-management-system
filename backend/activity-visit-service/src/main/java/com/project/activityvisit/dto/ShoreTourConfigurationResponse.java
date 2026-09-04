package com.project.activityvisit.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.project.activityvisit.model.enums.VisitTourStatus;

public record ShoreTourConfigurationResponse(

                UUID tourId,
                String tourCode,
                String tourName,
                String tourDescription,

                LocalDate startDate,
                LocalDate endDate,

                List<ScheduleConfiguration> schedules

) {

        public record ScheduleConfiguration(

                        UUID scheduleId,
                        Integer dayNumber,
                        LocalDate realDay,
                        String scheduleName,

                        List<ScheduleStopConfiguration> stops

        ) {
        }

        public record ScheduleStopConfiguration(

                        UUID scheduleStopId,

                        UUID portId,
                        String portName,

                        Integer stopOrder,

                        LocalDateTime arriveAt,
                        LocalDateTime leaveAt,

                        List<VisitTourConfiguration> visitTours

        ) {
        }

        public record VisitTourConfiguration(

                        UUID id,

                        String name,
                        String description,

                        LocalDateTime startTime,
                        LocalDateTime endTime,

                        Integer maxPassengers,

                        java.math.BigDecimal price,

                        VisitTourStatus status

        ) {
        }
}