package com.project.booking.service;

import com.project.booking.client.PaymentClient;
import com.project.booking.dto.payment.BillPaymentRequest;
import com.project.booking.dto.payment.BillPaymentResponse;
import org.springframework.stereotype.Service;

@Service
public class PaymentCheckoutServiceImpl implements PaymentCheckoutService {

    private final PaymentClient paymentClient;

    public PaymentCheckoutServiceImpl(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    @Override
    public BillPaymentResponse createPayment(BillPaymentRequest request) {
        return paymentClient.createBillPayment(request);
    }
}