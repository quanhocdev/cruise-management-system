package com.project.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VnPayConfig {

    @Value("${VNPAY_TMN_CODE}")
    private String tmnCode;

    @Value("${VNPAY_HASH_SECRET}")
    private String hashSecret;

    @Value("${VNPAY_PAYMENT_URL}")
    private String paymentUrl;

    @Value("${VNPAY_RETURN_URL}")
    private String returnUrl;

    public String getTmnCode() {
        return tmnCode;
    }

    public String getHashSecret() {
        return hashSecret;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }
}