package com.project.booking.controller;

import com.project.booking.dto.booking.BookingResponse;
import com.project.booking.dto.booking.CreateBookingRequest;
import com.project.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/passengers/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getMine(@RequestParam Long userId) {
        return ResponseEntity.ok(bookingService.getMine(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getById(@PathVariable Long id, @RequestParam Long userId,
            @RequestParam(defaultValue = "false") boolean privileged) {
        return ResponseEntity.ok(bookingService.get(id, userId, privileged));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @ModelAttribute CreateBookingRequest request,
            @RequestParam Long userId) {

        System.out.println("==========================================");
        System.out.println(">>> [CONTROLLER] ĐÃ NHẬN ĐƯỢC REQUEST ĐẶT VÉ!");
        System.out.println(">>> User ID: " + userId);
        System.out.println(">>> Tour ID: " + (request != null ? request.getTourId() : "NULL REQUEST"));
        System.out.println(">>> Số lượng hành khách: "
                + (request != null && request.getPassengers() != null ? request.getPassengers().size() : 0));
        System.out.println("==========================================");

        return ResponseEntity.ok(bookingService.create(request, userId));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id, @RequestParam Long userId) {
        return ResponseEntity.ok(bookingService.cancel(id, userId));
    }
}