
package com.project.notification.listener;

import com.project.common.event.BillPaidEvent;
import com.project.notification.service.NotificationBookingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BillPaidListener {

    private final NotificationBookingService notificationBookingService;

    public BillPaidListener(
            NotificationBookingService notificationBookingService) {
        this.notificationBookingService = notificationBookingService;
    }

    @KafkaListener(topics = "bill-paid-topic", groupId = "notification-service-bill-paid-group")
    public void handleBillPaid(BillPaidEvent event) {
        if (event == null || event.billId() == null) {
            throw new IllegalArgumentException(
                    "BillPaidEvent hoặc billId không được null");
        }

        System.out.println(
                ">>> [NOTIFICATION BILL PAID] Nhận event: billId="
                        + event.billId()
                        + ", bookingId=" + event.bookingId());

        notificationBookingService.sendCheckoutInvoiceEmail(event);
    }
}
