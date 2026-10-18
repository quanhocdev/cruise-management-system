package com.project.payment.refund;

import static com.project.payment.refund.RefundModels.*;
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
    private final RefundStore store;
    public RefundController(RefundStore store) { this.store=store; }

    @PostMapping("/{paymentId}/refunds")
    public ResponseEntity<Receipt> create(@PathVariable long paymentId,@Valid @RequestBody Request request,
                                         @AuthenticationPrincipal Jwt jwt) {
        long actor;
        try { actor=Long.parseLong(String.valueOf(jwt.getClaims().get("userId"))); }
        catch (Exception e) { throw new RefundException(403,"INVALID_ACTOR"); }
        if (actor<=0) throw new RefundException(403,"INVALID_ACTOR");
        Accepted accepted=store.create(paymentId,actor,request);
        // The durable receipt is stable across retries; progress is read from the GET endpoint.
        // Dispatch runs from the durable queue, outside this request transaction.
        return ResponseEntity.status(201).header("Idempotent-Replay",Boolean.toString(accepted.replay())).body(accepted.receipt());
    }

    @GetMapping("/{paymentId}/refunds")
    public Summary history(@PathVariable long paymentId,@RequestParam(defaultValue="0") int page,
                           @RequestParam(defaultValue="20") int size) { return store.summary(paymentId,page,size); }

    @GetMapping("/refunds/{refundId}")
    public Refund status(@PathVariable UUID refundId) { return store.read(refundId); }

    @ExceptionHandler(RefundException.class)
    public ResponseEntity<Map<String,String>> failure(RefundException e) {
        return ResponseEntity.status(e.status()).body(Map.of("code",e.getMessage(),"message",e.getMessage()));
    }
}
