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

    public BookingFinanceController(FinanceBookingService financeBookingService) {
        this.financeBookingService = financeBookingService;
    }

    // 1. Lấy danh sách booking thuộc về 1 Tour cụ thể (Dùng cho bảng hiển thị sau
    // khi chọn Tour)
    @GetMapping
    public ResponseEntity<List<BookingSummaryResponse>> getBookingsByTour(
            @RequestParam UUID tourId) {
        return ResponseEntity.ok(financeBookingService.getBookingsByTourId(tourId));
    }

    // 2. Lấy danh sách hành khách của một booking cụ thể (Dùng cho trang chi tiết
    // hành khách khi bấm vào 1 dòng booking)
    @GetMapping("/{bookingId}/passengers")
    public ResponseEntity<List<BookingPassengerDetailResponse>> getPassengersByBooking(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(financeBookingService.getPassengersByBookingId(bookingId));
    }
}