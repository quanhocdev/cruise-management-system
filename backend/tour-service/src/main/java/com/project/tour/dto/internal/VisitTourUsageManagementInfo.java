
package com.project.tour.dto.internal;

import com.project.tour.model.shore.enums.VisitTourStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record VisitTourUsageManagementInfo(
        UUID visitTourId,
        UUID tourId,
        String name,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer maxPassengers,
        BigDecimal price,
        VisitTourStatus visitTourStatus) {
}
