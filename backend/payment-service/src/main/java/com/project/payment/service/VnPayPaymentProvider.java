package com.project.payment.service;

import com.project.payment.config.VnPayConfig;
import com.project.payment.model.Payment;
import com.project.payment.model.enums.PaymentMethod;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

@Component
public class VnPayPaymentProvider implements PaymentProvider {

    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter VNP_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final VnPayConfig vnPayConfig;

    public VnPayPaymentProvider(
            VnPayConfig vnPayConfig) {
        this.vnPayConfig = vnPayConfig;
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.VNPAY;
    }

    @Override
    public String createPaymentUrl(
            Payment payment,
            String clientIp) {

        vnPayConfig.requireCredentials();

        String txnRef = String.valueOf(payment.getId());

        String createDate = LocalDateTime.ofInstant(payment.getCreatedAt(), VN_ZONE)
                .format(VNP_DATE_FORMAT);
        String expireDate = LocalDateTime.ofInstant(payment.getExpiresAt(), VN_ZONE)
                .format(VNP_DATE_FORMAT);

        long amount = payment.getAmount()
                .multiply(
                        BigDecimal.valueOf(100))
                .longValue();

        Map<String, String> params = new TreeMap<>();

        params.put(
                "vnp_Version",
                "2.1.0");

        params.put(
                "vnp_Command",
                "pay");

        params.put(
                "vnp_TmnCode",
                vnPayConfig.getTmnCode());

        params.put(
                "vnp_Amount",
                String.valueOf(amount));

        params.put(
                "vnp_CurrCode",
                "VND");

        params.put(
                "vnp_TxnRef",
                txnRef);

        params.put(
                "vnp_OrderInfo",
                "Thanh toan payment " + payment.getId());

        params.put(
                "vnp_OrderType",
                "other");

        params.put(
                "vnp_Locale",
                "vn");

        params.put(
                "vnp_ReturnUrl",
                vnPayConfig.getReturnUrl());

        params.put(
                "vnp_IpAddr",
                normalizeIp(clientIp));

        params.put(
                "vnp_CreateDate",
                createDate);

        params.put(
                "vnp_ExpireDate",
                expireDate);

        String query = buildQuery(params);

        String secureHash = hmacSHA512(
                vnPayConfig.getHashSecret(),
                query);

        return vnPayConfig.getPaymentUrl()
                + "?"
                + query
                + "&vnp_SecureHash="
                + secureHash;
    }

    @Override
    public boolean verifyCallback(
            Map<String, String> params) {

        vnPayConfig.requireCredentials();

        String receivedHash = params.get("vnp_SecureHash");

        if (receivedHash == null) {
            return false;
        }

        Map<String, String> data = new TreeMap<>();

        params.forEach((key, value) -> {
            if (key.startsWith("vnp_") && value != null && !value.isBlank()) {
                data.put(key, value);
            }
        });

        data.remove("vnp_SecureHash");
        data.remove("vnp_SecureHashType");

        String query = buildQuery(data);

        String calculatedHash = hmacSHA512(
                vnPayConfig.getHashSecret(),
                query);

        return MessageDigest.isEqual(
                calculatedHash.toLowerCase().getBytes(StandardCharsets.US_ASCII),
                receivedHash.toLowerCase().getBytes(StandardCharsets.US_ASCII));
    }

    @Override
    public String getTransactionCode(
            Map<String, String> params) {
        return params.get("vnp_TransactionNo");
    }

    private String buildQuery(
            Map<String, String> params) {

        StringBuilder query = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {

            if (entry.getValue() == null || entry.getValue().isBlank()) {
                continue;
            }

            if (query.length() > 0) {
                query.append("&");
            }

            query.append(
                    URLEncoder.encode(
                            entry.getKey(),
                            StandardCharsets.US_ASCII));

            query.append("=");

            query.append(
                    URLEncoder.encode(
                            entry.getValue(),
                            StandardCharsets.US_ASCII));
        }

        return query.toString();
    }

    private String normalizeIp(String clientIp) {
        if (clientIp == null || clientIp.isBlank()) return "127.0.0.1";
        String value = clientIp.split(",")[0].trim();
        return value.length() <= 45 ? value : "127.0.0.1";
    }

    private String hmacSHA512(
            String secretKey,
            String data) {

        try {

            Mac hmac = Mac.getInstance("HmacSHA512");

            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    secretKey.getBytes(
                            StandardCharsets.UTF_8),
                    "HmacSHA512");

            hmac.init(secretKeySpec);

            byte[] bytes = hmac.doFinal(
                    data.getBytes(
                            StandardCharsets.UTF_8));

            StringBuilder result = new StringBuilder();

            for (byte b : bytes) {

                result.append(
                        String.format(
                                "%02x",
                                b));
            }

            return result.toString();

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Cannot create VNPay secure hash",
                    e);
        }
    }
}
