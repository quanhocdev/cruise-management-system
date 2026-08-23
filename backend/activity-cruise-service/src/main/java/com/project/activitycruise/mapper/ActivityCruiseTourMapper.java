package com.project.activitycruise.mapper;

import com.project.activitycruise.dto.ActivityCruiseTourConfigRequest;
import com.project.activitycruise.dto.OnboardActivityCruiseTourResponse;
import com.project.activitycruise.model.ActivityCruise;
import com.project.activitycruise.model.ActivityCruiseTour;
import org.springframework.stereotype.Component;

@Component
public class ActivityCruiseTourMapper {

    public OnboardActivityCruiseTourResponse toResponse(ActivityCruiseTour assignment) {
        if (assignment == null) {
            return null;
        }

        ActivityCruise activity = assignment.getActivityCruise();

        return new OnboardActivityCruiseTourResponse(
                assignment.getId(),

                // Tour info
                assignment.getTourId(),
                null, // tourCode
                null, // tourName

                // Activity info
                activity != null ? activity.getId() : null,
                activity != null ? activity.getName() : null,
                activity != null ? activity.getDescription() : null,
                activity != null ? activity.getImageUrl() : null,

                // Cruise Area info
                assignment.getCruiseAreaId(),
                null, // cruiseAreaName

                // Configuration & Timings
                assignment.getStartTime(),
                assignment.getEndTime(),
                assignment.getMaxPassengers(),
                assignment.getPrice(),
                assignment.getStatus(),

                assignment.getCreatedAt(),
                assignment.getUpdatedAt());
    }

    /**
     * Gán cấu hình từ Request DTO và ActivityCruise Entity vào ActivityCruiseTour
     * Entity
     */
    public void applyConfig(
            ActivityCruiseTour assignment,
            ActivityCruiseTourConfigRequest request,
            ActivityCruise activityCruise) {

        if (assignment == null || request == null) {
            return;
        }

        assignment.setActivityCruise(activityCruise);
        assignment.setStartTime(request.startTime());
        assignment.setEndTime(request.endTime());
        assignment.setMaxPassengers(request.maxPassengers());
        assignment.setPrice(request.price());
    }
}