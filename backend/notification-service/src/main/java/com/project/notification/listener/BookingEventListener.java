package com.project.notification.listener;

import com.project.common.event.BookingConfirmedEvent;
import com.project.common.event.BookingCreatedEvent;
import com.project.notification.service.NotificationBookingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BookingEventListener {

    private final NotificationBookingService notificationBookingService;

    public BookingEventListener(NotificationBookingService notificationBookingService) {
        this.notificationBookingService = notificationBookingService;
    }

    // 1. Lắng nghe khi đơn hàng vừa được tạo (Chờ thanh toán - PENDING_PAYMENT)
    @KafkaListener(topics = "booking-created-topic", groupId = "notification-service-group")
    public void handleBookingCreated(BookingCreatedEvent event) {
        System.out.println("==================================================");
        System.out.println(">>> [NOTIFICATION LISTENER] Nhận được event tạo đơn mới (Chờ thanh toán)!");
        System.out.println(">>> Booking ID: " + event.bookingId());
        System.out.println("==================================================");

        // Gọi service lưu vào DB với type = PENDING_PAYMENT để hiện chuông thông báo
        notificationBookingService.savePendingPaymentNotification(event);
    }

    // 2. Lắng nghe khi đơn hàng đã thanh toán thành công (BOOKING_CONFIRMED /
    // PAYMENT_SUCCESS)
    @KafkaListener(topics = "booking-confirmed-topic", groupId = "notification-service-group")
    public void handleBookingConfirmed(BookingConfirmedEvent event) {
        System.out.println("==================================================");
        System.out.println(">>> [NOTIFICATION LISTENER] Nhận được event booking xác nhận thanh toán!");
        System.out.println(">>> Mã Booking: " + event.bookingCode());
        System.out.println(">>> Người nhận: " + event.recipientName() + " (" + event.recipientEmail() + ")");
        System.out.println("==================================================");

        // Ủy quyền cho Service thực hiện sinh mã QR (ZXing) và gửi email + lưu log DB
        notificationBookingService.sendBookingConfirmationEmail(event);
    }
}