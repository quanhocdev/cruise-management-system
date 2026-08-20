package com.project.tour.dto.tour.onboard;

import com.project.tour.model.enums.onboard.ActivityCruiseTourStatus;

import java.time.LocalDate;
import java.util.UUID;

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