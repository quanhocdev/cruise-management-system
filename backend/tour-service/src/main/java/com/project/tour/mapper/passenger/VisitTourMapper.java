package com.project.tour.mapper.passenger;

import org.springframework.stereotype.Component;

import com.project.tour.dto.passenger.activity.PassengerVisitTourDTO;
import com.project.tour.model.shore.VisitTour;

@Component("passengerVisitTourMapper")
public class VisitTourMapper {

    public PassengerVisitTourDTO toDTO(VisitTour visitTour) {
        if (visitTour == null) {
            return null;
        }

        return new PassengerVisitTourDTO(
                visitTour.getId(),
                visitTour.getScheduleStopId(),
                visitTour.getName(),
                visitTour.getDescription(),
                visitTour.getStartTime(),
                visitTour.getEndTime(),
                visitTour.getMaxPassengers(),
                visitTour.getPrice());
    }
}