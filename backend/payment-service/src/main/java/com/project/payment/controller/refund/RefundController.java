package com.project.payment.controller.refund;

import com.project.payment.service.refund.RefundException;
import com.project.payment.service.refund.RefundModels.Accepted;
import com.project.payment.service.refund.RefundModels.Refund;
import com.project.payment.service.refund.RefundModels.Request;
import com.project.payment.service.refund.RefundModels.Summary;
import com.project.payment.service.refund.RefundService;

import jakarta.validation.Valid;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@PreAuthorize("hasAnyRole('FINANCE','ADMIN')")
public class RefundController {

    private final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @PostMapping("/{paymentId}/refunds")
    public ResponseEntity<Accepted> create(
            @PathVariable Long paymentId,
            @Valid @RequestBody Request request,
            @AuthenticationPrincipal Jwt jwt) {

        UUID actorId;

        try {
            actorId = UUID.fromString(
                    String.valueOf(jwt.getClaims().get("userId")));

        } catch (Exception e) {

            throw new RefundException(
                    403,
                    "INVALID_ACTOR");
        }

        Accepted accepted = refundService.create(
                paymentId,
                actorId,
                request);

        return ResponseEntity
                .status(201)
                .header(
                        "Idempotent-Replay",
                        Boolean.toString(accepted.replay()))
                .body(accepted);
    }

    @GetMapping("/{paymentId}/refunds")
    public Summary history(
            @PathVariable Long paymentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return refundService.summary(
                paymentId,
                page,
                size);
    }

    @GetMapping("/refunds/{refundId}")
    public Refund status(
            @PathVariable UUID refundId) {

        return refundService.read(refundId);
    }

    @ExceptionHandler(RefundException.class)
    public ResponseEntity<Map<String, String>> failure(
            RefundException e) {

        return ResponseEntity
                .status(e.getStatus())
                .body(
                        Map.of(
                                "code", e.getMessage(),
                                "message", e.getMessage()));
    }
}