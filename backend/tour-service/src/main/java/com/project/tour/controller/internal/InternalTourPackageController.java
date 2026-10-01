package com.project.tour.controller.internal;

import com.project.tour.dto.tour.BookingPackageInfo;
import com.project.tour.service.tour.TourPackageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/tour-packages")
public class InternalTourPackageController {

    private final TourPackageService tourPackageService;

    public InternalTourPackageController(
            TourPackageService tourPackageService) {
        this.tourPackageService = tourPackageService;
    }

    @GetMapping("/{tourPackageId}/booking-info")
    public ResponseEntity<BookingPackageInfo> getBookingPackageInfo(
            @PathVariable UUID tourPackageId) {

        BookingPackageInfo info = tourPackageService.getBookingPackageInfo(tourPackageId);

        return ResponseEntity.ok(info);
    }
}