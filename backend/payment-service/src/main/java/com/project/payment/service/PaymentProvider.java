package com.project.payment.service;

import com.project.payment.model.Payment;
import com.project.payment.model.enums.PaymentMethod;

import java.util.Map;

public interface PaymentProvider {

    PaymentMethod getPaymentMethod();

    String createPaymentUrl(Payment payment, String clientIp);

    boolean verifyCallback(Map<String, String> params);

    String getTransactionCode(Map<String, String> params);
}
