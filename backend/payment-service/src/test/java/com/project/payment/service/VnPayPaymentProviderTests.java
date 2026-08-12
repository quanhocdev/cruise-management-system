package com.project.payment.service;

import com.project.payment.config.VnPayConfig;
import com.project.payment.model.Payment;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class VnPayPaymentProviderTests {
    private final VnPayPaymentProvider provider = new VnPayPaymentProvider(new VnPayConfig(
        "TESTCODE", "test-hash-secret", "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html",
        "http://localhost:8080/api/v1/payments/vnpay/return",
        "https://example.test/api/v1/payments/vnpay/ipn"));

    @Test void generatedUrlContainsValidHmacAndRequiredSandboxFields() throws Exception {
        Payment payment = payment();
        String url = provider.createPaymentUrl(payment, "203.0.113.10");
        Map<String, String> params = parseQuery(url);
        assertEquals("2.1.0", params.get("vnp_Version"));
        assertEquals("123456700", params.get("vnp_Amount"));
        assertEquals("203.0.113.10", params.get("vnp_IpAddr"));
        assertNotNull(params.get("vnp_ExpireDate"));
        assertTrue(provider.verifyCallback(params));
    }

    @Test void modifiedAmountInvalidatesSignature() throws Exception {
        Map<String, String> params = parseQuery(provider.createPaymentUrl(payment(), "127.0.0.1"));
        params.put("vnp_Amount", "99900");
        assertFalse(provider.verifyCallback(params));
    }

    private Payment payment() {
        Payment payment = new Payment(); payment.setId(42L); payment.setAmount(new BigDecimal("1234567.00"));
        payment.setCreatedAt(Instant.parse("2026-08-11T10:00:00Z"));
        payment.setExpiresAt(Instant.parse("2026-08-11T10:15:00Z")); return payment;
    }

    private Map<String, String> parseQuery(String url) throws Exception {
        Map<String, String> result = new HashMap<>();
        for (String pair : new URI(url).getRawQuery().split("&")) {
            String[] parts = pair.split("=", 2);
            result.put(URLDecoder.decode(parts[0], StandardCharsets.US_ASCII),
                URLDecoder.decode(parts[1], StandardCharsets.US_ASCII));
        }
        return result;
    }
}
