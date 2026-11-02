
package com.project.payment.service.refund;

import com.project.payment.model.PaymentRefund;
import com.project.payment.service.refund.RefundModels.Receipt;
import com.project.payment.service.refund.RefundModels.Refund;

import org.springframework.stereotype.Component;

@Component
public class RefundMapper {

    public Receipt toReceipt(PaymentRefund refund) {

        return new Receipt(
                refund.getId(),
                refund.getPaymentId(),
                refund.getAmount(),
                "ACCEPTED",
                refund.getCreatedAt());
    }

    public Refund toRefund(PaymentRefund refund) {

        return new Refund(
                refund.getId(),
                refund.getPaymentId(),
                refund.getActorId(),
                refund.getAmount(),
                refund.getReason(),
                refund.getStatus(),
                refund.getTransactionType(),
                refund.getOriginalTransactionNo(),
                refund.getOriginalCreatedAt(),
                refund.getProviderTransactionNo(),
                refund.getProviderCode(),
                refund.getCreatedAt(),
                refund.getUpdatedAt());
    }
}
