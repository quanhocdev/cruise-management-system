package com.project.tour.controller.internal;

import com.project.tour.dto.internal.TourBookingInfoResponse;
import com.project.tour.dto.scheduler.SchedulerTourDtos.TourDetailResponse;
import com.project.tour.service.internal.TourInternalService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/internal/tours")
public class TourInternalController {

    private final TourInternalService tourInternalService;

    public TourInternalController(TourInternalService tourInternalService) {
        this.tourInternalService = tourInternalService;
    }

    @GetMapping("/{tourId}/booking-info")
    public ResponseEntity<TourBookingInfoResponse> getTourBookingInfo(
            @PathVariable UUID tourId) {

        return ResponseEntity.ok(
                tourInternalService.getTourBookingInfo(tourId));
    }

    @GetMapping("/booking-info/batch")
    public ResponseEntity<List<TourBookingInfoResponse>> getToursBookingInfo(
            @RequestParam List<UUID> tourIds) {
        return ResponseEntity.ok(
                tourInternalService.getToursBookingInfo(tourIds));
    }

    @GetMapping("/{tourId}/tracking-details")
    public ResponseEntity<TourDetailResponse> getTourTrackingDetails(
            @PathVariable UUID tourId) {

        return ResponseEntity.ok(
                tourInternalService.getTourTrackingDetails(tourId));
    }

}