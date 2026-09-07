package com.project.payment.listener;

import com.project.common.event.BookingCreatedEvent;
import com.project.payment.service.PaymentEventConsumerService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BookingListener {

    private final PaymentEventConsumerService paymentEventConsumerService;

    public BookingListener(PaymentEventConsumerService paymentEventConsumerService) {
        this.paymentEventConsumerService = paymentEventConsumerService;
    }

    @KafkaListener(topics = "booking-created-topic", groupId = "payment-service-booking-group")
    public void handleBookingCreated(BookingCreatedEvent event) {
        System.out.println(">>> [KAFKA CONSUMER] Nhận được event booking ID: " + event.bookingId());
        paymentEventConsumerService.processBookingCreated(event);
    }
}