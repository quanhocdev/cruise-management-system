package com.project.tour.mapper.tour;

import com.project.tour.dto.scheduler.tour.CreateTourRequest;
import com.project.tour.dto.scheduler.tour.TourResponse;
import com.project.tour.dto.scheduler.tour.UpdateTourRequest;
import com.project.tour.model.Cruise;
import com.project.tour.model.Tour;

public final class TourMapper {

        private TourMapper() {
        }

        // CREATE
        public static Tour toEntity(
                        CreateTourRequest request) {

                Tour tour = new Tour();

                tour.setCode(request.code());
                tour.setName(request.name());
                tour.setDescription(request.description());
                tour.setStartDate(request.startDate());
                tour.setEndDate(request.endDate());
                return tour;
        }

        // UPDATE
        public static void updateEntity(
                        Tour tour,
                        UpdateTourRequest request) {

                tour.setCode(request.code());
                tour.setName(request.name());
                tour.setDescription(request.description());

                tour.setStartDate(request.startDate());
                tour.setEndDate(request.endDate());

        }

        // RESPONSE
        public static TourResponse toResponse(
                        Tour tour) {

                Cruise cruise = tour.getCruise();

                return new TourResponse(
                                tour.getId(),
                                tour.getCode(),
                                tour.getName(),
                                tour.getDescription(),

                                tour.getStartDate(),
                                tour.getEndDate(),

                                cruise != null
                                                ? cruise.getId()
                                                : null,

                                cruise != null
                                                ? cruise.getName()
                                                : null,

                                tour.getStatusTrip(),
                                tour.getBookingStart(),
                                tour.getBookingEnd(),
                                tour.getStatusBooking(),
                                tour.getCreatedAt(),
                                tour.getUpdatedAt());
        }
}