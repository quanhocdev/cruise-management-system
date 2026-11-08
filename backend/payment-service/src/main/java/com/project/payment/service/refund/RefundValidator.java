
package com.project.payment.service.refund;

import com.project.payment.dto.refund.RefundModels.Request;
import com.project.payment.model.Payment;
import com.project.payment.model.enums.PaymentMethod;
import com.project.payment.model.enums.PaymentStatus;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RefundValidator {

    public void validateRequest(Request request) {

        if (request == null
                || request.clientRequestId() == null
                || request.clientRequestId().version() != 4
                || request.clientRequestId().variant() != 2
                || request.amount() == null
                || request.amount().signum() <= 0
                || request.amount().stripTrailingZeros().scale() > 2
                || request.amount().compareTo(
                        new BigDecimal("9999999999.99")) > 0
                || request.reason() == null
                || request.reason().isBlank()
                || request.reason().length() > 1000) {

            throw new RefundException(
                    400,
                    "INVALID_REFUND_REQUEST");
        }
    }

    public void validatePayment(Payment payment) {

        if (payment == null) {
            throw new RefundException(
                    404,
                    "PAYMENT_NOT_FOUND");
        }

        if (payment.getStatus() != PaymentStatus.SUCCESS
                || payment.getMethod() != PaymentMethod.VNPAY) {

            throw new RefundException(
                    422,
                    "PAYMENT_NOT_REFUNDABLE");
        }

        if (payment.getPaidAt() == null
                || payment.getTransactionCode() == null
                || !payment.getTransactionCode().matches("[0-9]{1,15}")) {

            throw new RefundException(
                    422,
                    "PAYMENT_PROVIDER_REFERENCE_MISSING");
        }
    }
}
