package com.project.payment.controller;

import com.project.payment.dto.PaymentResponse;
import com.project.payment.exception.PaymentException;
import com.project.payment.model.enums.PaymentReferenceType;
import com.project.payment.model.enums.PaymentStatus;
import com.project.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/passenger/payments")
public class PassengerPaymentController {

    private final PaymentService paymentService;

    public PassengerPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<PaymentResponse> getPaymentByBookingId(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal Jwt jwt) {

        PaymentResponse response = paymentService.getPayments(bookingId, PaymentReferenceType.BOOKING)
                .stream()
                .filter(p -> p.getStatus() == PaymentStatus.PENDING)
                .findFirst()
                .orElseThrow(() -> new PaymentException("No active payment found for this booking"));

        return ResponseEntity.ok(response);
    }
}