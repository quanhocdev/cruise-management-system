package com.project.booking.listener;

import com.project.common.event.PaymentSuccessEvent;
import com.project.booking.service.BookingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentBookingListener {

    private final BookingService bookingService;

    public PaymentBookingListener(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @KafkaListener(topics = "payment-success-topic", groupId = "booking-service-group")
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        System.out.println(">>> [BOOKING LISTENER] Nhận event thanh toán cho Booking ID: " + event.bookingId());

        if ("SUCCESS".equalsIgnoreCase(event.status())) {
            // Giao phó toàn bộ nghiệp vụ xử lý đơn hàng cho Service
            bookingService.processPaymentSuccess(event.bookingId());
        }
    }
}