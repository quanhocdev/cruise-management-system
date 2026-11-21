
package com.project.tour.mapper.passenger;

import com.project.tour.dto.passenger.PassengerItineraryDayResponse;
import com.project.tour.dto.passenger.PassengerTourDetailResponse;
import com.project.tour.model.admin.Cruise;
import com.project.tour.model.scheduler.Schedule;
import com.project.tour.model.scheduler.Tour;

import java.util.List;

public class PassengerTourMapper {

        public static PassengerTourDetailResponse toDetailResponse(
                        Tour tour,
                        List<Schedule> schedules) {

                Cruise cruise = tour.getCruise();

                List<PassengerItineraryDayResponse> itinerary = schedules.stream()
                                .map(schedule -> new PassengerItineraryDayResponse(
                                                schedule.getId(),
                                                schedule.getDayNumber(),
                                                schedule.getRealDay(),
                                                schedule.getName(),
                                                schedule.getDescription()))
                                .toList();

                return new PassengerTourDetailResponse(
                                tour.getId(),
                                tour.getCode(),
                                tour.getName(),
                                tour.getDescription(),
                                tour.getStartDate(),
                                tour.getEndDate(),
                                tour.getBookingStart(),
                                tour.getBookingEnd(),
                                cruise != null ? cruise.getId() : null,
                                cruise != null ? cruise.getName() : null,
                                cruise != null ? cruise.getDescription() : null,
                                cruise != null ? cruise.getImageUrl() : null,
                                cruise != null ? cruise.getMaxPassengers() : null,
                                itinerary);
        }
}
