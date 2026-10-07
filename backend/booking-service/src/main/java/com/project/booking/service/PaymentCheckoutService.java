package com.project.booking.service;

import com.project.booking.dto.payment.BillPaymentRequest;
import com.project.booking.dto.payment.BillPaymentResponse;

public interface PaymentCheckoutService {

    BillPaymentResponse createPayment(BillPaymentRequest request);
}