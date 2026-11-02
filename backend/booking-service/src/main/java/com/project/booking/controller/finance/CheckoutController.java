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
    private final com.project.booking.service.command.IdempotentCommandService commands;

    public CheckoutController(CheckoutService checkoutService, com.project.booking.service.command.IdempotentCommandService commands) {
        this.checkoutService = checkoutService;
        this.commands = commands;
    }

    @GetMapping("/{bookingId}/checkout-preview")
    public ResponseEntity<CheckoutPreviewResponse> getCheckoutPreview(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                checkoutService.getCheckoutPreview(bookingId));
    }

    @PostMapping("/{bookingId}/checkout")
    public ResponseEntity<CheckoutResponse> confirmCheckout(
            @PathVariable Long bookingId,
            @jakarta.validation.Valid @RequestBody(required=false) com.project.booking.dto.finance.CheckoutConfirmRequest request) {
        if(request==null) return ResponseEntity.ok(checkoutService.confirmCheckout(bookingId));
        return commands.execute("CHECKOUT",request.clientRequestId(),java.util.Map.of("bookingId",bookingId,"previewVersion",request.previewVersion()),
                CheckoutResponse.class,()->checkoutService.prepareCheckout(bookingId,request.previewVersion()));
    }
}
