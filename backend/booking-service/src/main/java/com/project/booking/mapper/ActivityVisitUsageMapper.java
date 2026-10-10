package com.project.booking.mapper;

import com.project.booking.dto.shore.ActivityVisitUsageResponse;
import com.project.booking.model.ActivityVisitUsage;
import org.springframework.stereotype.Component;

@Component
public class ActivityVisitUsageMapper {

    public ActivityVisitUsageResponse toResponse(
            ActivityVisitUsage usage) {

        return new ActivityVisitUsageResponse(
                usage.getId(),
                usage.getBookingPassenger().getId(),
                usage.getVisitTourId(),
                usage.getQuantity(),
                usage.getUnitPrice(),
                usage.getDiscountAmount(),
                usage.getFinalAmount(),
                usage.getUsedAt());
    }
}