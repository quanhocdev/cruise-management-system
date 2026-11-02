package com.project.tour.service.route;

import com.project.tour.dto.route.RouteRequest;
import com.project.tour.dto.route.RouteResponse;

public interface RouteService {

    RouteResponse calculateRoute(RouteRequest request);
}
