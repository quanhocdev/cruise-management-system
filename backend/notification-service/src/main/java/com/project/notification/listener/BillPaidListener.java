
package com.project.notification.listener;

import com.project.common.event.BillPaidEvent;
import com.project.notification.service.NotificationBookingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BillPaidListener {

    private final NotificationBookingService notificationBookingService;
    private final org.springframework.jdbc.core.JdbcTemplate jdbc;

    public BillPaidListener(
            NotificationBookingService notificationBookingService, org.springframework.jdbc.core.JdbcTemplate jdbc) {
        this.jdbc=jdbc;
        this.notificationBookingService = notificationBookingService;
    }

    @KafkaListener(topics = "bill-paid-topic", groupId = "notification-service-bill-paid-group")
    @org.springframework.transaction.annotation.Transactional
    public void handleBillPaid(BillPaidEvent event) {
        if (event == null || event.billId() == null) {
            throw new IllegalArgumentException(
                    "BillPaidEvent hoặc billId không được null");
        }

        System.out.println(
                ">>> [NOTIFICATION BILL PAID] Nhận event: billId="
                        + event.billId()
                        + ", bookingId=" + event.bookingId());

        if(!"PAID".equals(event.billStatus())) throw new IllegalArgumentException("Bill is not paid");
        if(jdbc.update("INSERT INTO notification.bill_receipt_events(bill_id) VALUES (?) ON CONFLICT DO NOTHING",event.billId())==0)return;
        if(jdbc.queryForObject("SELECT count(*) FROM notification.notifications WHERE reference_type='BILL' AND reference_id=? AND type='BILL_PAID'",Long.class,event.billId())>0)return;
        notificationBookingService.sendCheckoutInvoiceEmail(event);
    }
}
