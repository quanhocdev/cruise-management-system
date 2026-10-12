package com.project.booking.service.finance.bill;

import com.project.booking.dto.payment.BillPaymentRequest;
import com.project.booking.dto.payment.BillPaymentResponse;

public interface PaymentCheckoutService {

    BillPaymentResponse createPayment(BillPaymentRequest request);
}