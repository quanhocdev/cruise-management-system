package com.project.booking.service;

import com.project.booking.dto.booking.CreateBookingRequest;
import com.project.booking.dto.booking.BookingResponse;
import com.project.booking.dto.booking.BookingPassengerResponse;
import com.project.booking.dto.BookingPaymentContext; // Thay đổi package DTO tùy theo project của bạn
import com.project.booking.dto.FeedbackEligibilityResponse;
import com.project.booking.dto.AvailableRoomResponse;
import java.util.List;
import java.time.LocalDate;
import java.util.UUID;

public interface BookingService {
    // 1. Tạo đơn đặt tour mới (sinh bookingCode, lưu thông tin hành khách & tính
    // tiền)
    BookingResponse create(CreateBookingRequest request, Long userId);

    // 2. Lấy thông tin chi tiết đơn hàng
    BookingResponse get(Long id, Long requesterId, boolean privileged);

    // 3. Lấy danh sách đơn hàng của tôi
    List<BookingResponse> getMine(Long userId);

    // 4. Hủy đơn hàng
    BookingResponse cancel(Long id, Long userId);

    // 5. Lấy ngữ cảnh thanh toán (truyền sang VNPAY)
    BookingPaymentContext getPaymentContext(Long id);

    // 6. Xác nhận thanh toán thành công từ VNPAY (cập nhật trạng thái CONFIRMED và
    // sinh mã QR ZXing)
    BookingResponse confirmPayment(Long id, Long paymentId);

    // 7. Tra cứu đơn hàng bằng mã bookingCode (dùng khi nhân viên quét QR check-in)
    BookingResponse getByCode(String bookingCode, Long requesterId, boolean privileged);

    // 8. Check-in cho hành khách bằng mã bookingCode của đơn
    BookingPassengerResponse checkInPassenger(String bookingCode, Long passengerId);

    // 9. Gửi nhắc nhở khởi hành tự động (Hỗ trợ cho DepartureReminderScheduler)
    int sendDepartureReminders(LocalDate departureDate);

    // 10. Kiểm tra điều kiện đánh giá tour sau khi đi về
    FeedbackEligibilityResponse getFeedbackEligibility(Long bookingId, Long userId);

    // 11. Lấy danh sách phòng trống theo Tour và Package
    List<AvailableRoomResponse> getAvailableRooms(UUID tourId, UUID tourPackageId);
}