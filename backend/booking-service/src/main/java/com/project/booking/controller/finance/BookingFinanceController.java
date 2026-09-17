package com.project.booking.controller.finance;

import com.project.booking.dto.booking.BookingSummaryResponse;
import com.project.booking.dto.passenger.BookingPassengerDetailResponse;
import com.project.booking.service.finance.FinanceBookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/finance/bookings")
public class BookingFinanceController {

    private final FinanceBookingService financeBookingService;

    public BookingFinanceController(
            FinanceBookingService financeBookingService) {
        this.financeBookingService = financeBookingService;
    }

    @GetMapping
    public ResponseEntity<List<BookingSummaryResponse>> getBookingsByTour(
            @RequestParam UUID tourId) {

        System.out.println("========================================");
        System.out.println("[FINANCE] GET /api/finance/bookings");
        System.out.println("[FINANCE] tourId = " + tourId);
        System.out.println("========================================");

        List<BookingSummaryResponse> result = financeBookingService.getBookingsByTourId(tourId);

        System.out.println("[FINANCE] bookings size = " + result.size());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{bookingId}/passengers")
    public ResponseEntity<List<BookingPassengerDetailResponse>> getPassengersByBooking(
            @PathVariable Long bookingId) {

        System.out.println("========================================");
        System.out.println("[FINANCE] GET passengers");
        System.out.println("[FINANCE] bookingId = " + bookingId);
        System.out.println("========================================");

        return ResponseEntity.ok(
                financeBookingService.getPassengersByBookingId(bookingId));
    }
}