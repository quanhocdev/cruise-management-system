
package com.project.payment.listener;

import com.project.common.event.RefundApprovedEvent;
import com.project.payment.service.refund.RefundApprovedEventConsumerService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class RefundApprovedListener {

    private final RefundApprovedEventConsumerService consumerService;

    public RefundApprovedListener(
            RefundApprovedEventConsumerService consumerService) {
        this.consumerService = consumerService;
    }

    @KafkaListener(topics = "refund-approved-topic", groupId = "payment-service-refund-group")
    public void handleRefundApproved(RefundApprovedEvent event) {

        System.out.println(
                ">>> [PAYMENT REFUND LISTENER] Nhận yêu cầu hoàn tiền."
                        + " BookingRefund ID: " + event.bookingRefundId()
                        + ", Booking ID: " + event.bookingId());

        consumerService.processRefundApproved(event);
    }
}
