package com.project.booking.mapper;

import com.project.booking.dto.ActivityCruiseUsageResponse;
import com.project.booking.model.ActivityCruiseUsage;
import org.springframework.stereotype.Component;

@Component
public class ActivityCruiseUsageMapper {

    public ActivityCruiseUsageResponse toResponse(
            ActivityCruiseUsage usage) {

        return new ActivityCruiseUsageResponse(
                usage.getId(),
                usage.getBookingPassenger().getId(),
                usage.getActivityCruiseTourId(),
                usage.getQuantity(),
                usage.getUnitPrice(),
                usage.getDiscountAmount(),
                usage.getFinalAmount(),
                usage.getUsedAt());
    }
}