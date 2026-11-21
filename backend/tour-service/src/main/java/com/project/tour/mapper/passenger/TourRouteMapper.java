package com.project.tour.mapper.passenger;

import com.project.tour.dto.passenger.route.PassengerRouteLegDTO;
import com.project.tour.dto.passenger.route.PassengerTourRouteDTO;
import com.project.tour.model.scheduler.TourRoute;
import com.project.tour.model.scheduler.TourRouteLeg;
import org.springframework.stereotype.Component;
import java.util.Comparator;
import java.util.List;

@Component("passengerTourRouteMapper")
public class TourRouteMapper {

    private final PortMapper portMapper;

    public TourRouteMapper(PortMapper portMapper) {
        this.portMapper = portMapper;
    }

    public PassengerRouteLegDTO toLegDTO(TourRouteLeg leg) {
        if (leg == null) {
            return null;
        }

        return new PassengerRouteLegDTO(
                leg.getId(),
                leg.getLegOrder(),
                portMapper.toDTO(leg.getFromPort()),
                portMapper.toDTO(leg.getToPort()),
                leg.getRouteGeoJson(),
                leg.getDistanceKm());
    }

    public PassengerTourRouteDTO toDTO(
            TourRoute route,
            List<TourRouteLeg> legs) {
        if (route == null) {
            return null;
        }

        List<PassengerRouteLegDTO> legDTOs = legs == null
                ? List.of()
                : legs.stream()
                        .sorted(Comparator.comparing(TourRouteLeg::getLegOrder))
                        .map(this::toLegDTO)
                        .toList();

        return new PassengerTourRouteDTO(
                route.getTour().getId(),
                route.getTour().getCode(),
                route.getTour().getName(),
                portMapper.toDTO(route.getDeparturePort()),
                portMapper.toDTO(route.getDestinationPort()),
                route.getRouteGeoJson(),
                route.getDistanceKm(),
                route.getDistanceFromDepartureKm(),
                route.getDistanceToDestinationKm(),
                legDTOs);
    }
}