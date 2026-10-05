package com.project.tour.dto.internal;

import com.project.tour.model.shore.enums.VisitTourStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record VisitTourUsageInfo(

        UUID visitTourId,

        UUID tourId,

        UUID scheduleStopId,

        String name,

        LocalDateTime startTime,

        LocalDateTime endTime,

        Integer maxPassengers,

        BigDecimal price,

        VisitTourStatus visitTourStatus,

        UUID packageBenefitId,

        Integer benefitQuantity,

        BigDecimal discountPercent) {
}