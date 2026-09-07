package com.project.payment.service;

import com.project.common.event.BookingCreatedEvent;
import com.project.payment.model.Payment;
import com.project.payment.model.enums.PaymentMethod;
import com.project.payment.model.enums.PaymentReferenceType;
import com.project.payment.model.enums.PaymentStatus;
import com.project.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class PaymentEventConsumerService {

    private final PaymentRepository repository;
    private final long timeoutMinutes;

    public PaymentEventConsumerService(PaymentRepository repository,
            @Value("${vnpay.payment-timeout-minutes:15}") long timeoutMinutes) {
        this.repository = repository;
        this.timeoutMinutes = timeoutMinutes;
    }

    @Transactional
    public void processBookingCreated(BookingCreatedEvent event) {
        // Kiểm tra chống trùng lặp (Idempotency)
        boolean exists = repository.findAllByReferenceIdAndReferenceTypeOrderByCreatedAtDesc(
                event.bookingId(), PaymentReferenceType.BOOKING).stream()
                .anyMatch(p -> p.getStatus() == PaymentStatus.PENDING);

        if (exists) {
            System.out
                    .println(">>> [PAYMENT SERVICE] Payment cho booking " + event.bookingId() + " đã tồn tại, bỏ qua.");
            return;
        }

        Instant now = Instant.now();
        Payment payment = new Payment();
        payment.setReferenceId(event.bookingId());
        payment.setPayerId(event.userId());
        payment.setReferenceType(PaymentReferenceType.BOOKING);
        payment.setAmount(event.totalPrice());
        payment.setMethod(PaymentMethod.VNPAY);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(now);
        payment.setUpdatedAt(now);
        payment.setExpiresAt(now.plus(timeoutMinutes, ChronoUnit.MINUTES));

        repository.save(payment);
        System.out.println(
                ">>> [PAYMENT SERVICE] Đã tự động tạo bản ghi thanh toán PENDING cho Booking ID: " + event.bookingId());
    }
}