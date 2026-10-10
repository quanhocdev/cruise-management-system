
package com.project.booking.listener;

import com.project.booking.service.finance.BillPaymentResultService;
import com.project.common.event.BillPaymentSuccessEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BillPaymentListener {

    private final BillPaymentResultService billPaymentResultService;

    public BillPaymentListener(
            BillPaymentResultService billPaymentResultService) {
        this.billPaymentResultService = billPaymentResultService;
    }

    @KafkaListener(topics = "bill-payment-success-topic", groupId = "bill-payment-service-group")
    public void handleBillPaymentSuccess(BillPaymentSuccessEvent event) {

        System.out.println(
                ">>> [BILL PAYMENT LISTENER] Nhận event thanh toán hóa đơn ID: "
                        + event.billId());

        if ("SUCCESS".equalsIgnoreCase(event.status())) {
            billPaymentResultService.processPaymentSuccess(event);
        }
    }
}
