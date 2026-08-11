package com.project.payment.controller;

import com.project.payment.dto.CreatePaymentRequest;
import com.project.payment.dto.PaymentResponse;
import com.project.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import com.project.payment.model.enums.PaymentReferenceType;
import java.util.List;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(
            PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest) {

        PaymentResponse response = paymentService.createPayment(
            request,
            userId(jwt),
            clientIp(httpRequest)
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(
        @PathVariable Long id,
        @AuthenticationPrincipal Jwt jwt,
        Authentication authentication
    ) {
        boolean privileged = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_FINANCE"));
        return ResponseEntity.ok(paymentService.getPayment(id, userId(jwt), privileged));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public ResponseEntity<List<PaymentResponse>> getPayments(
        @RequestParam Long referenceId,
        @RequestParam PaymentReferenceType referenceType
    ) {
        return ResponseEntity.ok(paymentService.getPayments(referenceId, referenceType));
    }

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        return forwardedFor == null || forwardedFor.isBlank()
            ? request.getRemoteAddr()
            : forwardedFor;
    }

    private Long userId(Jwt jwt) {
        Object claim = jwt.getClaim("userId");
        if (claim instanceof Number number) return number.longValue();
        try { return Long.valueOf(String.valueOf(claim)); }
        catch (Exception ex) { throw new com.project.payment.exception.PaymentException("JWT userId claim is missing or invalid"); }
    }
}
