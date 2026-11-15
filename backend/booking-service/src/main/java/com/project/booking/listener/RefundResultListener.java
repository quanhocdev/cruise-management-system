
package com.project.booking.listener;

import com.project.booking.service.finance.refund.FinanceRefundResultService;
import com.project.common.event.RefundResultEvent;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class RefundResultListener {

    private final FinanceRefundResultService refundResultService;

    public RefundResultListener(
            FinanceRefundResultService refundResultService) {
        this.refundResultService = refundResultService;
    }

    @KafkaListener(topics = "refund-result-topic", groupId = "booking-service-refund-group")
    public void handleRefundResult(RefundResultEvent event) {

        System.out.println(
                ">>> [BOOKING REFUND LISTENER] Nhận kết quả hoàn tiền."
                        + " PaymentRefund ID: " + event.paymentRefundId()
                        + ", ClientRequestId: "
                        + event.paymentClientRequestId()
                        + ", Status: " + event.status());

        refundResultService.processRefundResult(event);
    }
}
