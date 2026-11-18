package com.project.tour.mapper.passenger;

import com.project.tour.dto.passenger.route.PassengerPortDTO;
import com.project.tour.model.admin.Port;

public class PortMapper {

    public PassengerPortDTO toDTO(Port port) {
        if (port == null) {
            return null;
        }

        return new PassengerPortDTO(
                port.getId(),
                port.getName(),
                port.getCity(),
                port.getCountry(),
                port.getAddress(),
                port.getLatitude(),
                port.getLongitude(),
                port.getDescription());
    }
}