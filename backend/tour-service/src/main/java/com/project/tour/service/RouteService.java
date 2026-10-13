package com.project.tour.service;

import com.project.tour.dto.RouteRequest;
import com.project.tour.dto.RouteResponse;

public interface RouteService {

    RouteResponse calculateRoute(RouteRequest request);
}
