package com.project.tour.mapper.activitycruise;

import com.project.tour.dto.activitycruise.ActivityCruiseTourConfigRequest;
import com.project.tour.dto.activitycruise.ActivityCruiseTourResponse;
import com.project.tour.model.activitycruise.ActivityCruise;
import com.project.tour.model.activitycruise.ActivityCruiseTour;

import org.springframework.stereotype.Component;

@Component
public class ActivityCruiseTourMapper {

    public ActivityCruiseTourResponse toResponse(
            ActivityCruiseTour assignment) {

        if (assignment == null) {
            return null;
        }

        ActivityCruise activity = assignment.getActivityCruise();

        return new ActivityCruiseTourResponse(
                assignment.getId(),

                // =====================================================
                // TOUR
                // =====================================================

                assignment.getTourId(),
                null, // tourCode
                null, // tourName

                // =====================================================
                // ACTIVITY CRUISE
                // =====================================================

                activity != null
                        ? activity.getId()
                        : null,

                assignment.getActivityName(),

                assignment.getActivityDescription(),

                assignment.getImageUrl(),

                // =====================================================
                // CRUISE AREA
                // =====================================================

                assignment.getCruiseAreaId(),
                null, // cruiseAreaName

                // =====================================================
                // CONFIGURATION
                // =====================================================

                assignment.getStartTime(),
                assignment.getEndTime(),
                assignment.getMaxPassengers(),
                assignment.getPrice(),
                assignment.getStatus(),

                assignment.getCreatedAt(),
                assignment.getUpdatedAt());
    }

    /**
     * Gán cấu hình từ Request DTO và ActivityCruise Entity
     * vào ActivityCruiseTour Entity.
     *
     * ActivityCruiseTour giữ snapshot của:
     * - activityName
     * - activityDescription
     * - imageUrl
     */
    public void applyConfig(
            ActivityCruiseTour assignment,
            ActivityCruiseTourConfigRequest request,
            ActivityCruise activityCruise) {

        if (assignment == null || request == null) {
            return;
        }

        assignment.setActivityCruise(activityCruise);

        // =====================================================
        // SNAPSHOT ACTIVITY CRUISE
        // =====================================================

        if (activityCruise != null) {
            assignment.setActivityName(
                    activityCruise.getName());

            assignment.setActivityDescription(
                    activityCruise.getDescription());

            assignment.setImageUrl(
                    activityCruise.getImageUrl());
        }

        // =====================================================
        // CONFIGURATION
        // =====================================================

        assignment.setStartTime(
                request.startTime());

        assignment.setEndTime(
                request.endTime());

        assignment.setMaxPassengers(
                request.maxPassengers());

        assignment.setPrice(
                request.price());
    }
}
