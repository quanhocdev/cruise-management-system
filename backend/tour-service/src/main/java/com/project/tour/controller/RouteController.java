package com.project.tour.controller;

import com.project.tour.dto.route.RouteRequest;
import com.project.tour.dto.route.RouteResponse;
import com.project.tour.service.route.RouteService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tours/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping("/preview")
    public ResponseEntity<RouteResponse> previewRoute(
            @Valid @RequestBody RouteRequest request) {

        RouteResponse response = routeService.calculateRoute(request);

        return ResponseEntity.ok(response);
    }
}