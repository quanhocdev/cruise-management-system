package com.project.payment.service;

import com.project.payment.dto.BillPaymentRequest;
import com.project.payment.dto.BillPaymentResponse;
import com.project.payment.mapper.BillPaymentMapper;
import com.project.payment.model.Payment;
import com.project.payment.model.enums.PaymentMethod;
import com.project.payment.model.enums.PaymentReferenceType;
import com.project.payment.model.enums.PaymentStatus;
import com.project.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class BillPaymentServiceImpl implements BillPaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentProvider paymentProvider;
    private final BillPaymentMapper billPaymentMapper;

    private final long timeoutMinutes;

    public BillPaymentServiceImpl(
            PaymentRepository paymentRepository,
            PaymentProvider paymentProvider,
            BillPaymentMapper billPaymentMapper) {

        this.paymentRepository = paymentRepository;
        this.paymentProvider = paymentProvider;
        this.billPaymentMapper = billPaymentMapper;
        this.timeoutMinutes = 15;
    }

    @Override
    @Transactional
    public BillPaymentResponse createBillPayment(
            BillPaymentRequest request) {

        Instant now = Instant.now();

        Payment payment = new Payment();

        payment.setReferenceId(request.billId());
        payment.setPayerId(request.payerId());
        payment.setReferenceType(PaymentReferenceType.BILL);
        payment.setAmount(request.amount());
        payment.setMethod(PaymentMethod.VNPAY);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(now);
        payment.setUpdatedAt(now);
        payment.setExpiresAt(
                now.plus(timeoutMinutes, ChronoUnit.MINUTES));

        Payment savedPayment = paymentRepository.save(payment);

        String paymentUrl = paymentProvider.createPaymentUrl(
                savedPayment,
                "127.0.0.1");

        savedPayment.setPaymentUrl(paymentUrl);
        savedPayment.setUpdatedAt(Instant.now());

        Payment finalPayment = paymentRepository.save(savedPayment);

        return billPaymentMapper.toResponse(finalPayment);
    }
}