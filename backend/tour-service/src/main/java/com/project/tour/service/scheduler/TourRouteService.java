
package com.project.tour.service.scheduler;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.tour.dto.RouteRequest;
import com.project.tour.dto.RouteResponse;
import com.project.tour.dto.scheduler.TourRouteResponse;
import com.project.tour.mapper.scheduler.TourRouteMapper;
import com.project.tour.model.admin.Port;
import com.project.tour.model.enums.PortStatus;
import com.project.tour.model.scheduler.Tour;
import com.project.tour.model.scheduler.TourRoute;
import com.project.tour.repository.PortRepository;
import com.project.tour.repository.scheduler.TourRepository;
import com.project.tour.repository.scheduler.TourRouteRepository;
import com.project.tour.service.RouteService;

@Service
public class TourRouteService {

    private final TourRouteRepository tourRouteRepository;
    private final TourRepository tourRepository;
    private final PortRepository portRepository;
    private final RouteService routeService;

    public TourRouteService(
            TourRouteRepository tourRouteRepository,
            TourRepository tourRepository,
            PortRepository portRepository,
            RouteService routeService) {

        this.tourRouteRepository = tourRouteRepository;
        this.tourRepository = tourRepository;
        this.portRepository = portRepository;
        this.routeService = routeService;
    }

    @Transactional
    public TourRouteResponse createRoute(
            UUID tourId,
            UUID departurePortId,
            UUID destinationPortId) {

        if (departurePortId == null || destinationPortId == null) {
            throw new IllegalArgumentException("Both ports are required.");
        }

        if (departurePortId.equals(destinationPortId)) {
            throw new IllegalArgumentException(
                    "Departure port and destination port must be different.");
        }

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tour not found: " + tourId));

        if (tourRouteRepository.existsByTour_Id(tourId)) {
            throw new IllegalStateException(
                    "This tour already has an initial route.");
        }

        Port departurePort = findActivePort(departurePortId);
        Port destinationPort = findActivePort(destinationPortId);

        validatePortCoordinates(departurePort);
        validatePortCoordinates(destinationPort);

        RouteRequest request = new RouteRequest();
        request.setDepartureLongitude(
                departurePort.getLongitude().doubleValue());
        request.setDepartureLatitude(
                departurePort.getLatitude().doubleValue());
        request.setDestinationLongitude(
                destinationPort.getLongitude().doubleValue());
        request.setDestinationLatitude(
                destinationPort.getLatitude().doubleValue());

        RouteResponse routeResponse = routeService.calculateRoute(request);

        if (routeResponse == null
                || routeResponse.getGeoJson() == null
                || routeResponse.getDistanceKm() == null) {
            throw new IllegalStateException(
                    "SeaRoute returned incomplete route information.");
        }

        TourRoute tourRoute = new TourRoute();
        tourRoute.setTour(tour);
        tourRoute.setDeparturePort(departurePort);
        tourRoute.setDestinationPort(destinationPort);
        tourRoute.setRouteGeoJson(routeResponse.getGeoJson());
        tourRoute.setDistanceKm(routeResponse.getDistanceKm());
        tourRoute.setDistanceFromDepartureKm(
                routeResponse.getDistanceFromDepartureKm());
        tourRoute.setDistanceToDestinationKm(
                routeResponse.getDistanceToDestinationKm());

        tour.setTourRoute(tourRoute);

        TourRoute savedRoute = tourRouteRepository.save(tourRoute);

        return TourRouteMapper.toResponse(savedRoute);
    }

    @Transactional(readOnly = true)
    public TourRouteResponse getRouteByTourId(UUID tourId) {

        TourRoute route = tourRouteRepository.findByTour_Id(tourId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Initial route not found for tour: " + tourId));

        return TourRouteMapper.toResponse(route);
    }

    private Port findActivePort(UUID portId) {

        Port port = portRepository.findById(portId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Port not found: " + portId));

        if (port.getStatus() != PortStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Selected port is inactive: " + port.getName());
        }

        return port;
    }

    private void validatePortCoordinates(Port port) {

        BigDecimal latitude = port.getLatitude();
        BigDecimal longitude = port.getLongitude();

        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException(
                    "Port coordinates are missing: " + port.getName());
        }

        if (latitude.compareTo(BigDecimal.valueOf(-90)) < 0
                || latitude.compareTo(BigDecimal.valueOf(90)) > 0
                || longitude.compareTo(BigDecimal.valueOf(-180)) < 0
                || longitude.compareTo(BigDecimal.valueOf(180)) > 0) {
            throw new IllegalArgumentException(
                    "Invalid coordinates for port: " + port.getName());
        }
    }
}
