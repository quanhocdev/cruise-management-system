
package com.project.payment.service;

import com.project.payment.dto.BillPaymentRequest;
import com.project.payment.dto.BillPaymentResponse;
import com.project.payment.exception.PaymentException;
import com.project.payment.mapper.BillPaymentMapper;
import com.project.payment.model.Payment;
import com.project.payment.model.enums.PaymentMethod;
import com.project.payment.model.enums.PaymentReferenceType;
import com.project.payment.model.enums.PaymentStatus;
import com.project.payment.repository.PaymentRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class BillPaymentServiceImpl implements BillPaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentProvider paymentProvider;
    private final BillPaymentMapper billPaymentMapper;
    private final long timeoutMinutes;

    public BillPaymentServiceImpl(
            PaymentRepository paymentRepository,
            PaymentProvider paymentProvider,
            BillPaymentMapper billPaymentMapper,
            @Value("${vnpay.payment-timeout-minutes:15}") long timeoutMinutes) {

        this.paymentRepository = paymentRepository;
        this.paymentProvider = paymentProvider;
        this.billPaymentMapper = billPaymentMapper;
        this.timeoutMinutes = timeoutMinutes;
    }

    @Override
    @Transactional
    public BillPaymentResponse createBillPayment(BillPaymentRequest request) {

        Optional<Payment> existingPayment = paymentRepository
                .findByReferenceIdAndReferenceType(
                        request.billId(),
                        PaymentReferenceType.BILL);

        Instant now = Instant.now();
        Payment payment;

        if (existingPayment.isPresent()) {
            payment = existingPayment.get();

            if (!request.payerId().equals(payment.getPayerId())) {
                throw new PaymentException(
                        "You cannot pay for this bill");
            }

            if (request.amount().compareTo(payment.getAmount()) != 0) {
                throw new PaymentException(
                        "Payment amount does not match bill total");
            }

            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                throw new PaymentException(
                        "This bill has already been paid");
            }

            if (payment.getStatus() != PaymentStatus.PENDING) {
                throw new PaymentException(
                        "Payment is not pending; cannot reuse this payment");
            }

        } else {
            payment = new Payment();
            payment.setReferenceId(request.billId());
            payment.setPayerId(request.payerId());
            payment.setReferenceType(PaymentReferenceType.BILL);
            payment.setAmount(request.amount());
            payment.setMethod(PaymentMethod.VNPAY);
            payment.setStatus(PaymentStatus.PENDING);
            payment.setCreatedAt(now);
        }

        payment.setMethod(PaymentMethod.VNPAY);
        payment.setUpdatedAt(now);
        payment.setExpiresAt(
                now.plus(timeoutMinutes, ChronoUnit.MINUTES));

        // Tạo URL mới cho phiên VNPay hiện tại.
        payment.setPaymentUrl(
                paymentProvider.createPaymentUrl(payment, "127.0.0.1"));

        Payment savedPayment = paymentRepository.save(payment);

        return billPaymentMapper.toResponse(savedPayment);
    }
}
