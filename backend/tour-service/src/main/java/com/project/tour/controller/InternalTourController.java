package com.project.tour.controller;

import com.project.tour.dto.schedule.ScheduleBookingContext;
import com.project.tour.exception.AppException;
import com.project.tour.model.Schedule;
import com.project.tour.model.Tour;
import com.project.tour.model.enums.ScheduleStatus;
import com.project.tour.model.enums.tour.TourBookingStatus;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.repository.tour.TourRepository;
import com.project.tour.repository.tour.schedule.ScheduleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/internal/tours")
public class InternalTourController {
    private final TourRepository tourRepository;
    private final ScheduleRepository scheduleRepository;
    private final byte[] expectedApiKey;

    public InternalTourController(TourRepository tourRepository,
                                  ScheduleRepository scheduleRepository,
                                  @Value("${internal.api-key}") String apiKey) {
        this.tourRepository = tourRepository;
        this.scheduleRepository = scheduleRepository;
        this.expectedApiKey = apiKey.getBytes(StandardCharsets.UTF_8);
    }

    @GetMapping("/{id}/booking-context")
    public ScheduleBookingContext bookingContext(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String apiKey) {
        authorize(apiKey);
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new AppException("Tour not found: " + id, HttpStatus.NOT_FOUND));
        Schedule departure = scheduleRepository
                .findFirstByTour_IdAndStatusOrderByRealDayAsc(id, ScheduleStatus.ACTIVE)
                .orElseThrow(() -> new AppException("Tour has no active schedule", HttpStatus.CONFLICT));
        return new ScheduleBookingContext(
                tour.getId(),
                tour.getCruise().getMaxPassengers(),
                departure.getRealDay(),
                bookingStatus(tour));
    }

    private String bookingStatus(Tour tour) {
        LocalDateTime now = LocalDateTime.now();
        boolean withinWindow = (tour.getBookingStart() == null || !now.isBefore(tour.getBookingStart()))
                && (tour.getBookingEnd() == null || !now.isAfter(tour.getBookingEnd()));
        return tour.getStatusBooking() == TourBookingStatus.OPEN
                && tour.getStatusTrip() == TourStatusTrip.UPCOMING
                && withinWindow ? "OPEN" : "CLOSED";
    }

    private void authorize(String suppliedApiKey) {
        byte[] supplied = suppliedApiKey == null
                ? new byte[0]
                : suppliedApiKey.getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(expectedApiKey, supplied)) {
            throw new AppException("Invalid internal API key", HttpStatus.UNAUTHORIZED);
        }
    }
}
