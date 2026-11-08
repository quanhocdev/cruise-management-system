package com.project.chat.listener;

import com.project.chat.service.ConfirmedBookingService;
import com.project.common.event.BookingConfirmedEvent;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BookingConfirmedListener {

    private final ConfirmedBookingService confirmedBookingService;

    public BookingConfirmedListener(
            ConfirmedBookingService confirmedBookingService) {
        this.confirmedBookingService = confirmedBookingService;
    }

    @KafkaListener(
            topics = "booking-confirmed-topic",
            groupId = "chat-service-group")
    public void handleBookingConfirmed(BookingConfirmedEvent event) {

        System.out.println("==================================================");
        System.out.println(">>> [CHAT LISTENER] Nhận booking xác nhận thành công!");
        System.out.println(">>> Booking ID: " + event.bookingId());
        System.out.println(">>> Booking Code: " + event.bookingCode());
        System.out.println(">>> Passenger ID: " + event.recipientUserId());
        System.out.println("==================================================");

        confirmedBookingService.saveConfirmedBooking(event);
    }
}