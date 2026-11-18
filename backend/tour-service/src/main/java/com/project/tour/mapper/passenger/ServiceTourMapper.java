package com.project.tour.mapper.passenger;

import com.project.tour.dto.passenger.service.PassengerServiceDTO;
import com.project.tour.model.convenience.service.ServiceTour;

public class ServiceTourMapper {

    public PassengerServiceDTO toDTO(ServiceTour serviceTour) {
        if (serviceTour == null || serviceTour.getService() == null) {
            return null;
        }

        var service = serviceTour.getService();

        return new PassengerServiceDTO(
                service.getId(),
                serviceTour.getId(),
                serviceTour.getCruiseAreaId(),
                service.getName(),
                service.getDescription(),
                service.getPrice(),
                serviceTour.getDurationMinutes() != null
                        ? serviceTour.getDurationMinutes()
                        : service.getDurationMinutes(),
                serviceTour.getMaxPassengers() != null
                        ? serviceTour.getMaxPassengers()
                        : service.getMaxPassengers(),
                service.getImageUrl());
    }
}