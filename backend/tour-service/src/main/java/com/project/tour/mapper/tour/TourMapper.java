package com.project.tour.mapper.tour;

import com.project.tour.dto.tour.CreateTourRequest;
import com.project.tour.dto.tour.TourResponse;
import com.project.tour.dto.tour.UpdateTourRequest;
import com.project.tour.model.Cruise;
import com.project.tour.model.Tour;

public class TourMapper {

    public static Tour toEntity(
            CreateTourRequest request,
            Cruise cruise) {

        Tour tour = new Tour();

        tour.setCode(request.getCode());
        tour.setName(request.getName());
        tour.setDescription(request.getDescription());
        tour.setDayStart(request.getDayStart());
        tour.setDayEnd(request.getDayEnd());
        tour.setCruise(cruise);
        tour.setBookingStart(request.getBookingStart());
        tour.setBookingEnd(request.getBookingEnd());

        return tour;
    }

    public static void updateEntity(
            Tour tour,
            UpdateTourRequest request,
            Cruise cruise) {

        tour.setCode(request.getCode());
        tour.setName(request.getName());
        tour.setDescription(request.getDescription());
        tour.setDayStart(request.getDayStart());
        tour.setDayEnd(request.getDayEnd());
        tour.setCruise(cruise);
        tour.setStatusTrip(request.getStatusTrip());
        tour.setBookingStart(request.getBookingStart());
        tour.setBookingEnd(request.getBookingEnd());
        tour.setStatusBooking(request.getStatusBooking());
    }

    public static TourResponse toResponse(Tour tour) {

        TourResponse response = new TourResponse();

        response.setId(tour.getId());
        response.setCode(tour.getCode());
        response.setName(tour.getName());
        response.setDescription(tour.getDescription());
        response.setDayStart(tour.getDayStart());
        response.setDayEnd(tour.getDayEnd());

        if (tour.getCruise() != null) {
            response.setCruiseId(tour.getCruise().getId());
            response.setCruiseName(tour.getCruise().getName());
        }

        response.setStatusTrip(tour.getStatusTrip());
        response.setBookingStart(tour.getBookingStart());
        response.setBookingEnd(tour.getBookingEnd());
        response.setStatusBooking(tour.getStatusBooking());
        response.setCreatedAt(tour.getCreatedAt());
        response.setUpdatedAt(tour.getUpdatedAt());

        return response;
    }
}