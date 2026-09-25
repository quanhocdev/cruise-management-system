package com.project.tour.mapper.tour.operation;

import com.project.tour.dto.tour.operation.AssignmentServiceResponse;
import com.project.tour.model.convenience.service.ServiceTour;

import java.time.LocalDateTime;
import java.time.ZoneId;

public final class AssignmentServiceMapper {

    private AssignmentServiceMapper() {
    }

    public static AssignmentServiceResponse toResponse(
            ServiceTour serviceTour) {

        if (serviceTour == null) {
            return null;
        }

        return new AssignmentServiceResponse(
                serviceTour.getId(),
                serviceTour.getTourId(),
                serviceTour.getCruiseAreaId(),
                serviceTour.getId(),

                serviceTour.getService() != null
                        ? serviceTour.getService().getId()
                        : null,

                serviceTour.getService() != null
                        ? serviceTour.getService().getName()
                        : null,

                serviceTour.getService() != null
                        ? serviceTour.getService().getDescription()
                        : null,

                serviceTour.getService() != null
                        ? serviceTour.getService().getPrice()
                        : null,

                serviceTour.getMaxPassengers(),
                serviceTour.getDurationMinutes(),

                serviceTour.getService() != null
                        ? serviceTour.getService().getImageUrl()
                        : null,

                serviceTour.getStatus() != null
                        ? serviceTour.getStatus().name()
                        : null,

                toLocalDateTime(serviceTour.getCreatedAt()),
                toLocalDateTime(serviceTour.getUpdatedAt()));
    }

    private static LocalDateTime toLocalDateTime(
            java.time.Instant instant) {

        if (instant == null) {
            return null;
        }

        return LocalDateTime.ofInstant(
                instant,
                ZoneId.systemDefault());
    }
}