
package com.project.booking.dto.shore;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityVisitUsageManagementInfo(
        UUID visitTourId,
        UUID tourId,
        String name,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer maxPassengers,
        BigDecimal price,
        String visitTourStatus) {
}
