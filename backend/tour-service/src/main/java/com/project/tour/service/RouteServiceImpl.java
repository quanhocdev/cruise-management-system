package com.project.tour.service;

import com.project.tour.dto.RouteRequest;
import com.project.tour.dto.RouteResponse;
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

        Map<String, Object> geoJson = createGeoJson(route);

        Map<String, Object> attributes = route.getAttributes();

        Double distanceKm = getNumericAttribute(attributes, "distKM");
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
}
