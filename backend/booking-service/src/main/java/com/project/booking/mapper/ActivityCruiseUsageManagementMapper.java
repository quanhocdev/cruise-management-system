package com.project.booking.mapper;

import com.project.booking.dto.onboard.ActivityCruiseUsageManagementInfo;
import com.project.booking.dto.onboard.ActivityCruiseUsageManagementResponse;
import com.project.booking.model.ActivityCruiseUsage;
import org.springframework.stereotype.Component;

@Component
public class ActivityCruiseUsageManagementMapper {

    public ActivityCruiseUsageManagementResponse toResponse(
            ActivityCruiseUsage usage,
            ActivityCruiseUsageManagementInfo activityInfo) {

        return new ActivityCruiseUsageManagementResponse(
                usage.getId(),
                usage.getBookingPassenger().getId(),
                usage.getBookingPassenger().getPassenger().getFullName(),
                usage.getBookingPassenger().getBooking().getBookingCode(),

                usage.getActivityCruiseTourId(),
                activityInfo != null
                        ? activityInfo.activityName()
                        : null,
                activityInfo != null
                        ? activityInfo.startTime()
                        : null,
                activityInfo != null
                        ? activityInfo.endTime()
                        : null,
                activityInfo != null
                        ? activityInfo.maxPassengers()
                        : null,
                activityInfo != null
                        ? activityInfo.price()
                        : null,
                activityInfo != null
                        ? activityInfo.activityCruiseTourStatus()
                        : null,

                usage.getQuantity(),
                usage.getUnitPrice(),
                usage.getDiscountAmount(),
                usage.getFinalAmount(),
                usage.getUsedAt());
    }
}