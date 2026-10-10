package com.project.payment.service;

import com.project.payment.dto.BillPaymentRequest;
import com.project.payment.dto.BillPaymentResponse;

public interface BillPaymentService {

    BillPaymentResponse createBillPayment(BillPaymentRequest request);
}