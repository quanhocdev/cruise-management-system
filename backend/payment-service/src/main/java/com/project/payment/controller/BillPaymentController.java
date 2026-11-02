package com.project.payment.controller;

import com.project.payment.dto.*;
import com.project.payment.service.BillPaymentServiceImpl;
import com.project.payment.repository.PaymentRepository;
import com.project.payment.mapper.BillPaymentMapper;
import com.project.payment.model.enums.PaymentReferenceType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

@RestController
@RequestMapping("/internal/payments/bills")
public class BillPaymentController {
    private final BillPaymentServiceImpl service;
    private final PaymentRepository payments;
    private final BillPaymentMapper mapper;
    private final String key;
    public BillPaymentController(BillPaymentServiceImpl service,PaymentRepository payments,BillPaymentMapper mapper,
            @Value("${internal.api-key:${INTERNAL_API_KEY:}}") String key) {
        this.service=service; this.payments=payments; this.mapper=mapper; this.key=key;
    }
    private void authorize(String supplied) {
        if(key.isBlank() || supplied==null || !MessageDigest.isEqual(key.getBytes(StandardCharsets.UTF_8),supplied.getBytes(StandardCharsets.UTF_8)))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
    @PostMapping
    public ResponseEntity<BillPaymentResponse> create(@RequestHeader(value="X-Internal-Api-Key",required=false) String supplied,@RequestBody BillPaymentRequest request) {
        authorize(supplied); return service.command(request);
    }
    @GetMapping("/{billId}")
    public List<BillPaymentResponse> list(@RequestHeader(value="X-Internal-Api-Key",required=false) String supplied,@PathVariable Long billId) {
        authorize(supplied); return payments.findAllByReferenceIdAndReferenceTypeOrderByCreatedAtDesc(billId,PaymentReferenceType.BILL).stream().map(mapper::toResponse).toList();
    }
}
