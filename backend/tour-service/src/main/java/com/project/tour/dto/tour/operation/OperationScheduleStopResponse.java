package com.project.tour.dto.tour.operation;

import java.time.LocalDateTime;
import java.util.UUID;

public record OperationScheduleStopResponse(

        UUID id,

        UUID scheduleId,

        UUID portId,

        String portName,

        Integer stopOrder,

        LocalDateTime arriveAt,

        LocalDateTime leaveAt

) {
}