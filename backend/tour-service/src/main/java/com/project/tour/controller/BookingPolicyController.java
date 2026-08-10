package com.project.tour.controller;

import com.project.tour.dto.policy.*;
import com.project.tour.service.BookingPolicyService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/v1/policies/{policyId}/booking-rules")
public class BookingPolicyController {
    private final BookingPolicyService service;
    public BookingPolicyController(BookingPolicyService service) { this.service = service; }
    @PostMapping
    public ResponseEntity<BookingPolicyResponse> create(@PathVariable UUID policyId, @Valid @RequestBody CreateBookingPolicyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(policyId, request));
    }
    @GetMapping
    public ResponseEntity<List<BookingPolicyResponse>> getAll(@PathVariable UUID policyId, @RequestParam(defaultValue = "false") boolean activeOnly) {
        return ResponseEntity.ok(service.getAll(policyId, activeOnly));
    }
    @PutMapping("/{ruleId}")
    public ResponseEntity<BookingPolicyResponse> update(@PathVariable UUID policyId, @PathVariable UUID ruleId, @Valid @RequestBody UpdateBookingPolicyRequest request) {
        return ResponseEntity.ok(service.update(policyId, ruleId, request));
    }
    @PatchMapping("/{ruleId}/deactivate")
    public ResponseEntity<BookingPolicyResponse> deactivate(@PathVariable UUID policyId, @PathVariable UUID ruleId) {
        return ResponseEntity.ok(service.deactivate(policyId, ruleId));
    }
}
