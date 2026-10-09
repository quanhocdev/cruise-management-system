
package com.project.booking.mapper;

import com.project.booking.dto.shore.ActivityVisitUsageManagementInfo;
import com.project.booking.dto.shore.ActivityVisitUsageManagementResponse;
import com.project.booking.model.ActivityVisitUsage;
import org.springframework.stereotype.Component;

@Component
public class ActivityVisitUsageManagementMapper {

    public ActivityVisitUsageManagementResponse toResponse(
            ActivityVisitUsage usage,
            ActivityVisitUsageManagementInfo visitInfo) {

        return new ActivityVisitUsageManagementResponse(
                usage.getId(),
                usage.getBookingPassenger().getId(),
                usage.getBookingPassenger().getPassenger().getFullName(),
                usage.getBookingPassenger().getBooking().getBookingCode(),

                usage.getVisitTourId(),
                visitInfo != null ? visitInfo.name() : null,
                visitInfo != null ? visitInfo.startTime() : null,
                visitInfo != null ? visitInfo.endTime() : null,
                visitInfo != null ? visitInfo.maxPassengers() : null,
                visitInfo != null ? visitInfo.price() : null,
                visitInfo != null
                        ? visitInfo.visitTourStatus()
                        : null,

                usage.getQuantity(),
                usage.getUnitPrice(),
                usage.getDiscountAmount(),
                usage.getFinalAmount(),
                usage.getUsedAt());
    }
}
