package com.project.payment.controller;

import com.project.payment.dto.PaymentResponse;
import com.project.payment.model.enums.PaymentReferenceType;
import com.project.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/passenger/payments")
public class PassengerPaymentController {

    private final PaymentService paymentService;

    public PassengerPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<?> getPaymentByBookingId(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal Jwt jwt) {

        List<PaymentResponse> payments = paymentService.getPayments(bookingId, PaymentReferenceType.BOOKING);

        if (payments == null || payments.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Payment is still processing via Kafka"));
        }

        // Trả về 200 OK kèm bản ghi thanh toán mới nhất
        return ResponseEntity.ok(payments.get(0));
    }
}

// 9704198526191432198
// NGUYEN VAN A
// 07/15