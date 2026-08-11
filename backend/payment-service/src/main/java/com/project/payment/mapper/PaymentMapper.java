package com.project.payment.mapper;

import com.project.payment.dto.CreatePaymentRequest;
import com.project.payment.dto.PaymentResponse;
import com.project.payment.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Payment toEntity(CreatePaymentRequest request) {
        if (request == null) {
            return null;
        }

        Payment payment = new Payment();

        payment.setReferenceId(request.getReferenceId());
        payment.setReferenceType(request.getReferenceType());
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getMethod());

        return payment;
    }

    public PaymentResponse toResponse(Payment payment) {
        if (payment == null) {
            return null;
        }

        PaymentResponse response = new PaymentResponse();

        response.setId(payment.getId());
        response.setReferenceId(payment.getReferenceId());
        response.setReferenceType(payment.getReferenceType());
        response.setAmount(payment.getAmount());
        response.setMethod(payment.getMethod());
        response.setStatus(payment.getStatus());
        response.setTransactionCode(payment.getTransactionCode());
        response.setResponseCode(payment.getResponseCode());
        response.setPaymentUrl(payment.getPaymentUrl());
        response.setCreatedAt(payment.getCreatedAt());
        response.setPaidAt(payment.getPaidAt());
        response.setExpiresAt(payment.getExpiresAt());

        return response;
    }
}
