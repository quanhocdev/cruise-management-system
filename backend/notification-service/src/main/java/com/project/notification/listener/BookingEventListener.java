package com.project.notification.listener;

import com.project.common.event.BookingConfirmedEvent;
import com.project.notification.service.EmailDeliveryService; // Thay thế bằng service gửi mail thực tế của bạn
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BookingEventListener {

    private final EmailDeliveryService emailDeliveryService;

    public BookingEventListener(EmailDeliveryService emailDeliveryService) {
        this.emailDeliveryService = emailDeliveryService;
    }

    @KafkaListener(topics = "booking-confirmed-topic", groupId = "notification-service-group")
    public void handleBookingConfirmed(BookingConfirmedEvent event) {
        System.out.println("==================================================");
        System.out.println(">>> [NOTIFICATION LISTENER] Nhận được event booking xác nhận!");
        System.out.println(">>> Mã Booking: " + event.bookingCode());
        System.out.println(">>> Người nhận: " + event.recipientName() + " (" + event.recipientEmail() + ")");
        System.out.println("==================================================");

        // Ủy quyền cho Service thực hiện sinh mã QR (ZXing) và gửi email
        emailDeliveryService.sendBookingConfirmationEmail(event);
    }
}