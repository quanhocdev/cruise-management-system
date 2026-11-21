package com.project.tour.controller.helper;

import com.project.tour.dto.route.RouteRequest;
import com.project.tour.dto.route.RouteResponse;
import com.project.tour.dto.route.TourRouteLegResponse;
import com.project.tour.dto.route.TourRouteOverviewResponse;
import com.project.tour.service.route.RouteService;
import com.project.tour.service.route.TourRouteQueryService;
import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tours/routes")
public class RouteController {

    private final RouteService routeService;
    private final TourRouteQueryService tourRouteQueryService;

    public RouteController(RouteService routeService, TourRouteQueryService tourRouteQueryService) {
        this.routeService = routeService;
        this.tourRouteQueryService = tourRouteQueryService;
    }

    @PostMapping("/preview")
    public ResponseEntity<RouteResponse> previewRoute(
            @Valid @RequestBody RouteRequest request) {

        RouteResponse response = routeService.calculateRoute(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/tours/{tourId}/overview")
    public ResponseEntity<TourRouteOverviewResponse> getRouteOverview(@PathVariable UUID tourId) {
        TourRouteOverviewResponse overview = tourRouteQueryService.getOverviewByTourId(tourId);
        return ResponseEntity.ok(overview);
    }

    @GetMapping("/tours/{tourId}/legs")
    public ResponseEntity<List<TourRouteLegResponse>> getRouteLegs(@PathVariable UUID tourId) {
        List<TourRouteLegResponse> legs = tourRouteQueryService.getLegsByTourId(tourId);
        return ResponseEntity.ok(legs);
    }
}