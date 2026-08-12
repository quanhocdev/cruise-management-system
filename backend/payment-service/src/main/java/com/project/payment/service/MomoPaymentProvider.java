package com.project.payment.service;

import com.project.payment.model.Payment;
import com.project.payment.model.enums.PaymentMethod;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MomoPaymentProvider implements PaymentProvider {

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.MOMO;
    }

    @Override
    public String createPaymentUrl(
            Payment payment,
            String clientIp) {

        throw new UnsupportedOperationException(
                "MoMo payment provider is not implemented yet");
    }

    @Override
    public boolean verifyCallback(
            Map<String, String> params) {

        throw new UnsupportedOperationException(
                "MoMo payment provider is not implemented yet");
    }

    @Override
    public String getTransactionCode(
            Map<String, String> params) {

        return params.get("transactionCode");
    }
}
