package com.project.payment.controller;

import com.project.payment.dto.BillPaymentRequest;
import com.project.payment.dto.BillPaymentResponse;
import com.project.payment.service.BillPaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments/bills")
public class BillPaymentController {

    private final BillPaymentService billPaymentService;

    public BillPaymentController(BillPaymentService billPaymentService) {
        this.billPaymentService = billPaymentService;
    }

    @PostMapping
    public ResponseEntity<BillPaymentResponse> createBillPayment(
            @RequestBody BillPaymentRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(billPaymentService.createBillPayment(request));
    }
}