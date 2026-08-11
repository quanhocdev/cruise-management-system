package com.project.payment.service;

import com.project.payment.dto.CreatePaymentRequest;
import com.project.payment.dto.PaymentResponse;

public interface PaymentService {

    PaymentResponse createPayment(CreatePaymentRequest request);

    PaymentResponse handleVnPayCallback(
            java.util.Map<String, String> params);
}
