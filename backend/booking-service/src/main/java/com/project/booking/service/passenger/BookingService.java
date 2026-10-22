package com.project.booking.service.passenger;

import com.project.booking.dto.booking.CreateBookingRequest;
import com.project.booking.dto.booking.BookingResponse;
import java.util.List;
import java.time.LocalDate;
import java.util.UUID;

public interface BookingService {
    // 1. Tạo đơn đặt tour mới
    BookingResponse create(CreateBookingRequest request, UUID userId, String email);

    // 2. Lấy thông tin chi tiết đơn hàng
    BookingResponse get(Long id, UUID requesterId, boolean privileged);

    // 3. Lấy danh sách đơn hàng cá nhân
    List<BookingResponse> getMine(UUID userId);

    // 4. Hủy đơn hàng (chỉ khi CONFIRMED)
    BookingResponse cancel(Long id, UUID userId);

    // 5. Gửi nhắc nhở khởi hành tự động
    int sendDepartureReminders(LocalDate departureDate);

    void processPaymentSuccess(Long bookingId);
}