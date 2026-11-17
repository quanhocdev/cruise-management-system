package com.project.tour.service.route;

import com.project.tour.dto.route.RouteRequest;
import com.project.tour.dto.route.RouteResponse;

import eu.europa.ec.eurostat.jgiscotools.feature.Feature;
import eu.europa.ec.eurostat.searoute.SeaRouting;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RouteServiceImpl implements RouteService {

    private final SeaRouting seaRouting;

    public RouteServiceImpl() {
        this.seaRouting = new SeaRouting();
    }

    @Override
    public RouteResponse calculateRoute(RouteRequest request) {

        System.out.println("\n========== SEAROUTE REQUEST ==========");
        System.out.println("Departure Longitude: " + request.getDepartureLongitude());
        System.out.println("Departure Latitude: " + request.getDepartureLatitude());
        System.out.println("Destination Longitude: " + request.getDestinationLongitude());
        System.out.println("Destination Latitude: " + request.getDestinationLatitude());

        Feature route = seaRouting.getRoute(
                request.getDepartureLongitude(),
                request.getDepartureLatitude(),
                request.getDestinationLongitude(),
                request.getDestinationLatitude());

        if (route == null || route.getGeometry() == null) {
            throw new IllegalStateException(
                    "SeaRoute could not calculate a route between the given coordinates.");
        }

        Geometry geometry = route.getGeometry();
        Map<String, Object> attributes = route.getAttributes();

        System.out.println("========== SEAROUTE RESPONSE ==========");
        System.out.println("Geometry type: " + geometry.getGeometryType());
        System.out.println("Geometry: " + geometry);
        System.out.println("Attributes: " + attributes);

        Double distanceKm = getNumericAttribute(attributes, "distKM");

        System.out.println("Distance from SeaRoute: " + distanceKm);
        System.out.println("Geometry calculated distance: "
                + calculateGeometryDistanceKm(geometry));

        System.out.println("========================================\n");

        Map<String, Object> geoJson = createGeoJson(route);

        Double distanceFromDepartureKm = getNumericAttribute(attributes, "dFromKM");
        Double distanceToDestinationKm = getNumericAttribute(attributes, "dToKM");

        return new RouteResponse(
                geoJson,
                distanceKm,
                distanceFromDepartureKm,
                distanceToDestinationKm);
    }

    private Map<String, Object> createGeoJson(Feature feature) {
        Map<String, Object> geoJson = new LinkedHashMap<>();

        geoJson.put("type", "Feature");
        geoJson.put("properties", new LinkedHashMap<>(feature.getAttributes()));
        geoJson.put("geometry", geometryToGeoJson(feature.getGeometry()));

        return geoJson;
    }

    private Map<String, Object> geometryToGeoJson(Geometry geometry) {
        Map<String, Object> result = new LinkedHashMap<>();

        if (geometry instanceof LineString lineString) {
            result.put("type", "LineString");
            result.put("coordinates", toCoordinateList(lineString.getCoordinates()));
            return result;
        }

        if (geometry instanceof MultiLineString multiLineString) {
            List<List<List<Double>>> lines = new ArrayList<>();

            for (int i = 0; i < multiLineString.getNumGeometries(); i++) {
                LineString lineString = (LineString) multiLineString.getGeometryN(i);

                lines.add(toCoordinateList(lineString.getCoordinates()));
            }

            result.put("type", "MultiLineString");
            result.put("coordinates", lines);
            return result;
        }

        throw new IllegalArgumentException(
                "Unsupported route geometry: " + geometry.getGeometryType());
    }

    private List<List<Double>> toCoordinateList(Coordinate[] coordinates) {
        List<List<Double>> result = new ArrayList<>();

        for (Coordinate coordinate : coordinates) {
            List<Double> point = new ArrayList<>();
            point.add(coordinate.x);
            point.add(coordinate.y);
            result.add(point);
        }

        return result;
    }

    private Double getNumericAttribute(
            Map<String, Object> attributes,
            String attributeName) {

        Object value = attributes.get(attributeName);

        if (value instanceof Number number) {
            return number.doubleValue();
        }

        if (value != null) {
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        return null;
    }

    private double calculateGeometryDistanceKm(Geometry geometry) {
        double total = 0.0;

        for (int i = 0; i < geometry.getNumGeometries(); i++) {
            Geometry part = geometry.getGeometryN(i);
            Coordinate[] coordinates = part.getCoordinates();

            for (int j = 0; j < coordinates.length - 1; j++) {
                total += haversineKm(
                        coordinates[j].y,
                        coordinates[j].x,
                        coordinates[j + 1].y,
                        coordinates[j + 1].x);
            }
        }

        return total;
    }

    private double haversineKm(
            double lat1, double lon1,
            double lat2, double lon2) {

        double earthRadius = 6371.0;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        return 2 * earthRadius
                * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
