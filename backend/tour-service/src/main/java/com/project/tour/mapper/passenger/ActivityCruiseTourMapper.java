
package com.project.tour.mapper.passenger;

import com.project.tour.dto.passenger.activity.PassengerActivityCruiseDTO;
import com.project.tour.model.onboard.ActivityCruiseTour;

import org.springframework.stereotype.Component;

@Component("passengerActivityCruiseTourMapper")
public class ActivityCruiseTourMapper {

        public PassengerActivityCruiseDTO toDTO(
                        ActivityCruiseTour activityCruiseTour) {

                if (activityCruiseTour == null) {
                        return null;
                }

                var activityCruise = activityCruiseTour.getActivityCruise();

                String name = activityCruiseTour.getActivityName() != null
                                ? activityCruiseTour.getActivityName()
                                : activityCruise != null
                                                ? activityCruise.getName()
                                                : null;

                String description = activityCruiseTour.getActivityDescription() != null
                                ? activityCruiseTour.getActivityDescription()
                                : activityCruise != null
                                                ? activityCruise.getDescription()
                                                : null;

                String imageUrl = activityCruiseTour.getImageUrl() != null
                                ? activityCruiseTour.getImageUrl()
                                : activityCruise != null
                                                ? activityCruise.getImageUrl()
                                                : null;

                return new PassengerActivityCruiseDTO(
                                activityCruise != null ? activityCruise.getId() : null,
                                activityCruiseTour.getId(),
                                activityCruiseTour.getCruiseAreaId(),
                                name,
                                description,
                                imageUrl,
                                activityCruiseTour.getStartTime(),
                                activityCruiseTour.getEndTime(),
                                activityCruiseTour.getMaxPassengers(),
                                activityCruiseTour.getPrice());
        }
}
