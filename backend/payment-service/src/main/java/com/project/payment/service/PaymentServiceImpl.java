package com.project.payment.service;

import com.project.payment.dto.CreatePaymentRequest;
import com.project.payment.dto.PaymentResponse;
import com.project.payment.mapper.PaymentMapper;
import com.project.payment.model.Payment;
import com.project.payment.model.enums.PaymentMethod;
import com.project.payment.model.enums.PaymentStatus;
import com.project.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    private final Map<PaymentMethod, PaymentProvider> paymentProviders;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            PaymentMapper paymentMapper,
            List<PaymentProvider> providers) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;

        this.paymentProviders = new EnumMap<>(PaymentMethod.class);

        for (PaymentProvider provider : providers) {
            this.paymentProviders.put(
                    provider.getPaymentMethod(),
                    provider);
        }
    }

    @Override
    @Transactional
    public PaymentResponse createPayment(
            CreatePaymentRequest request) {

        Payment payment = paymentMapper.toEntity(request);

        payment.setStatus(
                PaymentStatus.PENDING);

        payment.setCreatedAt(
                Instant.now());

        /*
         * Lưu Payment trước để có ID.
         *
         * ID này sẽ được sử dụng làm vnp_TxnRef
         * khi tạo URL thanh toán VNPay.
         */
        Payment savedPayment = paymentRepository.save(payment);

        PaymentProvider provider = getProvider(request.getMethod());

        String paymentUrl = provider.createPaymentUrl(
                savedPayment);

        savedPayment.setPaymentUrl(
                paymentUrl);

        savedPayment = paymentRepository.save(
                savedPayment);

        return paymentMapper.toResponse(
                savedPayment);
    }

    @Override
    @Transactional
    public PaymentResponse handleVnPayCallback(
            Map<String, String> params) {

        PaymentProvider provider = getProvider(
                PaymentMethod.VNPAY);

        /*
         * Kiểm tra chữ ký do VNPay gửi về.
         */
        if (!provider.verifyCallback(params)) {
            throw new IllegalArgumentException(
                    "Invalid VNPay callback signature");
        }

        /*
         * vnp_TxnRef là ID Payment của hệ thống mình.
         *
         * Ví dụ:
         *
         * Payment ID = 15
         *
         * => vnp_TxnRef = 15
         */
        String txnRef = params.get("vnp_TxnRef");

        if (txnRef == null) {
            throw new IllegalArgumentException(
                    "Missing VNPay transaction reference");
        }

        Long paymentId;

        try {
            paymentId = Long.parseLong(txnRef);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Invalid VNPay transaction reference");
        }

        /*
         * Tìm Payment bằng ID.
         */
        Payment payment = paymentRepository
                .findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Payment not found: "
                                + paymentId));

        /*
         * Mã giao dịch thực tế do VNPay trả về.
         *
         * vnp_TransactionNo khác với vnp_TxnRef.
         */
        String transactionCode = params.get("vnp_TransactionNo");

        /*
         * VNPay response code:
         *
         * 00 = thanh toán thành công
         * khác 00 = thanh toán thất bại
         */
        String responseCode = params.get("vnp_ResponseCode");

        if ("00".equals(responseCode)) {

            payment.setStatus(
                    PaymentStatus.SUCCESS);

            payment.setPaidAt(
                    Instant.now());

        } else {

            payment.setStatus(
                    PaymentStatus.FAILED);
        }

        /*
         * Lưu transaction code thực tế
         * VNPay trả về.
         */
        payment.setTransactionCode(
                transactionCode);

        Payment savedPayment = paymentRepository.save(
                payment);

        return paymentMapper.toResponse(
                savedPayment);
    }

    private PaymentProvider getProvider(
            PaymentMethod method) {

        PaymentProvider provider = paymentProviders.get(
                method);

        if (provider == null) {
            throw new IllegalArgumentException(
                    "Unsupported payment method: "
                            + method);
        }

        return provider;
    }
}
