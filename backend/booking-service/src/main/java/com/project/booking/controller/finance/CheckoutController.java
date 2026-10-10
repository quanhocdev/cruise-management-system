package com.project.booking.controller.finance;

import com.project.booking.dto.finance.CheckoutPreviewResponse;
import com.project.booking.dto.finance.CheckoutResponse;
import com.project.booking.service.finance.checkout.CheckoutService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance/bookings")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @GetMapping("/{bookingId}/checkout-preview")
    public ResponseEntity<CheckoutPreviewResponse> getCheckoutPreview(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                checkoutService.getCheckoutPreview(bookingId));
    }

    @PostMapping("/{bookingId}/checkout")
    public ResponseEntity<CheckoutResponse> confirmCheckout(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                checkoutService.confirmCheckout(bookingId));
    }
}