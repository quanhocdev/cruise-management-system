package com.project.booking.controller;

import com.project.booking.dto.*;
import com.project.booking.exception.BookingException;
import com.project.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/internal/bookings")
public class InternalBookingController {
    private final BookingService service;
    private final byte[] expectedKey;
    public InternalBookingController(BookingService service, @Value("${internal.api-key}") String key) {
        this.service = service; this.expectedKey = key.getBytes(StandardCharsets.UTF_8);
    }
    @GetMapping("/{id}/payment-context")
    BookingPaymentContext context(@PathVariable Long id, @RequestHeader(value = "X-Internal-Api-Key", required = false) String key) {
        authorize(key); return service.getPaymentContext(id);
    }
    @PutMapping("/{id}/payment-confirmation")
    BookingResponse confirm(@PathVariable Long id, @Valid @RequestBody ConfirmBookingPaymentRequest request,
                            @RequestHeader(value = "X-Internal-Api-Key", required = false) String key) {
        authorize(key); return service.confirmPayment(id, request.paymentId());
    }
    private void authorize(String key) {
        byte[] actual = key == null ? new byte[0] : key.getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(expectedKey, actual))
            throw new BookingException(HttpStatus.UNAUTHORIZED, "Invalid internal API key");
    }
}
