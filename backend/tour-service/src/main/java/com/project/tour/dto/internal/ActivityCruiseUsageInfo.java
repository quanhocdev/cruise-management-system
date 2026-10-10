package com.project.tour.dto.internal;

import com.project.tour.model.onboard.enums.ActivityCruiseTourStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityCruiseUsageInfo(

        UUID activityCruiseTourId,

        UUID tourId,

        UUID activityCruiseId,

        String activityName,

        LocalDateTime startTime,

        LocalDateTime endTime,

        Integer maxPassengers,

        BigDecimal price,

        ActivityCruiseTourStatus activityCruiseTourStatus,

        boolean activityActive,

        UUID packageBenefitId,

        Integer benefitQuantity,

        BigDecimal discountPercent) {

}