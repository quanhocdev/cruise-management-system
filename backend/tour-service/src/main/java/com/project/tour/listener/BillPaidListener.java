package com.project.tour.listener;

import com.project.common.event.BillPaidEvent;
import com.project.tour.service.NfcCardService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BillPaidListener {

    private final NfcCardService nfcCardService;

    public BillPaidListener(NfcCardService nfcCardService) {
        this.nfcCardService = nfcCardService;
    }

    @KafkaListener(topics = "bill-paid-topic", groupId = "tour-service-bill-paid-group")
    public void handleBillPaid(BillPaidEvent event) {
        if (event == null) {
            throw new IllegalArgumentException(
                    "BillPaidEvent không được null");
        }

        System.out.println(
                "[TOUR BILL PAID] Nhận event: billId=" + event.billId()
                        + ", bookingId=" + event.bookingId());

        nfcCardService.releaseAssignedCards(event.nfcCardUids());
    }
}