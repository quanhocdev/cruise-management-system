package com.project.tour.dto.route;

import java.util.Map;

public class RouteResponse {

    private Map<String, Object> geoJson;
    private Double distanceKm;
    private Double distanceFromDepartureKm;
    private Double distanceToDestinationKm;

    public RouteResponse() {
    }

    public RouteResponse(
            Map<String, Object> geoJson,
            Double distanceKm,
            Double distanceFromDepartureKm,
            Double distanceToDestinationKm) {
        this.geoJson = geoJson;
        this.distanceKm = distanceKm;
        this.distanceFromDepartureKm = distanceFromDepartureKm;
        this.distanceToDestinationKm = distanceToDestinationKm;
    }

    public Map<String, Object> getGeoJson() {
        return geoJson;
    }

    public void setGeoJson(Map<String, Object> geoJson) {
        this.geoJson = geoJson;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Double getDistanceFromDepartureKm() {
        return distanceFromDepartureKm;
    }

    public void setDistanceFromDepartureKm(Double distanceFromDepartureKm) {
        this.distanceFromDepartureKm = distanceFromDepartureKm;
    }

    public Double getDistanceToDestinationKm() {
        return distanceToDestinationKm;
    }

    public void setDistanceToDestinationKm(Double distanceToDestinationKm) {
        this.distanceToDestinationKm = distanceToDestinationKm;
    }
}
