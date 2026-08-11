package com.project.payment.controller;

import com.project.payment.dto.PaymentResponse;
import com.project.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentCallbackController {

    private final PaymentService paymentService;

    public PaymentCallbackController(
            PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/vnpay/callback")
    public ResponseEntity<PaymentResponse> vnpayCallback(
            @RequestParam Map<String, String> params) {

        PaymentResponse response = paymentService.handleVnPayCallback(params);

        return ResponseEntity.ok(response);
    }
}