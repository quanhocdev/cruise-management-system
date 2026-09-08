package com.project.booking.listener;

import com.project.booking.model.Booking;
import com.project.booking.model.enums.BookingStatus;
import com.project.booking.repository.BookingRepository;
import com.project.common.event.PaymentSuccessEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PaymentBookingListener {

    private final BookingRepository bookingRepository;

    public PaymentBookingListener(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @KafkaListener(topics = "payment-success-topic", groupId = "booking-service-group")
    @Transactional
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        System.out
                .println(">>> [BOOKING SERVICE] Nhận được Kafka event thanh toán cho Booking ID: " + event.bookingId());

        // Kiểm tra xem trạng thái thanh toán có phải là SUCCESS thật không
        if ("SUCCESS".equalsIgnoreCase(event.status())) {
            Booking booking = bookingRepository.findById(event.bookingId()).orElse(null);

            if (booking != null) {
                // Chỉ cập nhật nếu đơn hàng đang ở trạng thái chờ thanh toán
                if (booking.getStatus() == BookingStatus.PENDING_PAYMENT) {
                    booking.setStatus(BookingStatus.CONFIRMED); // Chuyển sang trạng thái thành công/đã xác nhận
                    bookingRepository.save(booking);
                    System.out.println(">>> [BOOKING SERVICE] Đã cập nhật thành công đơn hàng #" + event.bookingId()
                            + " thành CONFIRMED!");
                } else {
                    System.out.println(">>> [BOOKING SERVICE] Đơn hàng #" + event.bookingId() + " đã ở trạng thái: "
                            + booking.getStatus());
                }
            } else {
                System.out.println(">>> [BOOKING SERVICE] Không tìm thấy Booking ID: " + event.bookingId());
            }
        }
    }
}