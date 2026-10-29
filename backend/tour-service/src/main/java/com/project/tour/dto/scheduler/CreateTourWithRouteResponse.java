
package com.project.tour.dto.scheduler;

import com.project.tour.dto.scheduler.tour.TourResponse;

public record CreateTourWithRouteResponse(
        TourResponse tour,
        TourRouteResponse route) {
}
