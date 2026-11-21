
package com.project.tour.mapper.route;

import com.project.tour.dto.route.RouteRequest;
import com.project.tour.model.admin.Port;

import org.springframework.stereotype.Component;

@Component
public class RouteRequestMapper {

    public RouteRequest fromPorts(Port departurePort, Port destinationPort) {
        if (departurePort == null || destinationPort == null) {
            throw new IllegalArgumentException(
                    "Departure port and destination port are required");
        }

        RouteRequest request = new RouteRequest();

        request.setDepartureLongitude(toDouble(departurePort.getLongitude()));
        request.setDepartureLatitude(toDouble(departurePort.getLatitude()));

        request.setDestinationLongitude(toDouble(destinationPort.getLongitude()));
        request.setDestinationLatitude(toDouble(destinationPort.getLatitude()));

        return request;
    }

    private Double toDouble(java.math.BigDecimal value) {
        return value != null ? value.doubleValue() : null;
    }
}
