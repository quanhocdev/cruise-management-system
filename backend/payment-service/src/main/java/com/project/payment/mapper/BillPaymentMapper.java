package com.project.payment.mapper;

import com.project.payment.dto.BillPaymentResponse;
import com.project.payment.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class BillPaymentMapper {

    public BillPaymentResponse toResponse(Payment payment) {

        return new BillPaymentResponse(
                payment.getId(),
                payment.getReferenceId(),
                payment.getPayerId(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getTransactionCode(),
                payment.getPaymentUrl(),
                payment.getCreatedAt(),
                payment.getExpiresAt());
    }
}