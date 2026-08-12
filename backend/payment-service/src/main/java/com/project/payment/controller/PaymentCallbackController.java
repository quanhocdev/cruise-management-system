package com.project.payment.controller;

import com.project.payment.dto.PaymentResponse;
import com.project.payment.dto.VnPayIpnResponse;
import com.project.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/payments/vnpay")
public class PaymentCallbackController {

    private final PaymentService paymentService;
    private final String frontendResultUrl;

    public PaymentCallbackController(
            PaymentService paymentService,
            @Value("${vnpay.frontend-result-url:http://localhost:5173/payment/result}") String frontendResultUrl) {
        this.paymentService = paymentService;
        this.frontendResultUrl = frontendResultUrl;
    }

    @GetMapping("/return")
    public ResponseEntity<Void> vnpayReturn(
            @RequestParam Map<String, String> params) {

        PaymentResponse payment = paymentService.handleVnPayReturn(params);
        URI location = UriComponentsBuilder.fromUriString(frontendResultUrl)
            .queryParam("paymentId", payment.getId())
            .queryParam("status", payment.getStatus())
            .build().toUri();

        return ResponseEntity.status(302)
            .header(HttpHeaders.LOCATION, location.toString())
            .build();
    }

    @GetMapping("/ipn")
    public ResponseEntity<VnPayIpnResponse> vnpayIpn(
            @RequestParam Map<String, String> params) {

        return ResponseEntity.ok(paymentService.handleVnPayIpn(params));
    }
}
