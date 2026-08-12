package com.project.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VnPayConfig {

    private final String tmnCode;
    private final String hashSecret;
    private final String paymentUrl;
    private final String returnUrl;
    private final String ipnUrl;

    public VnPayConfig(
        @Value("${vnpay.tmn-code:}") String tmnCode,
        @Value("${vnpay.hash-secret:}") String hashSecret,
        @Value("${vnpay.payment-url}") String paymentUrl,
        @Value("${vnpay.return-url}") String returnUrl,
        @Value("${vnpay.ipn-url}") String ipnUrl
    ) {
        this.tmnCode = tmnCode;
        this.hashSecret = hashSecret;
        this.paymentUrl = paymentUrl;
        this.returnUrl = returnUrl;
        this.ipnUrl = ipnUrl;
    }

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

    public String getIpnUrl() { return ipnUrl; }

    public void requireCredentials() {
        if (tmnCode == null || tmnCode.isBlank() || hashSecret == null || hashSecret.isBlank()) {
            throw new IllegalStateException(
                "VNPay Sandbox credentials are missing. Set VNPAY_TMN_CODE and VNPAY_HASH_SECRET"
            );
        }
    }
}
