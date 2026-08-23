package com.project.activitycruise.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.project.activitycruise.model.enums.ActivityCruiseTourStatus;

public record OnboardActivityCruiseTourResponse(

                UUID id,

                UUID tourId,
                String tourCode,
                String tourName,

                LocalDate tourStartDate,
                LocalDate tourEndDate,

                UUID cruiseAreaId,
                String cruiseAreaName,

                ActivityCruiseTourStatus status

) {
}