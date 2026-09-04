package com.project.booking.controller;

import com.project.booking.dto.BookingResponse;
import com.project.booking.dto.NfcCheckInRequest;
import com.project.booking.dto.PassengerVoyageResponse;
import com.project.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/check-in")
public class CheckInController {
    private final BookingService service;

    public CheckInController(BookingService service) { this.service = service; }

    @GetMapping("/bookings/{bookingCode}")
    BookingResponse lookup(@PathVariable String bookingCode) {
        return service.getByCode(bookingCode, null, true);
    }

    @PostMapping("/bookings/{bookingCode}/passengers/{passengerVoyageId}")
    PassengerVoyageResponse checkIn(@PathVariable String bookingCode,
                                    @PathVariable Long passengerVoyageId,
                                    @Valid @RequestBody NfcCheckInRequest request) {
        return service.checkIn(bookingCode, passengerVoyageId, request.nfcTagId());
    }

    @PostMapping("/nfc/{nfcTagId}/board")
    PassengerVoyageResponse board(@PathVariable String nfcTagId) {
        return service.board(nfcTagId);
    }

    @PostMapping("/nfc/{nfcTagId}/disembark")
    PassengerVoyageResponse disembark(@PathVariable String nfcTagId) {
        return service.disembark(nfcTagId);
    }
}
