package com.project.booking.service;

import com.project.booking.dto.booking.CreateBookingRequest;
import com.project.booking.dto.booking.BookingResponse;
import com.project.booking.dto.AvailableRoomResponse;
import java.util.List;
import java.time.LocalDate;
import java.util.UUID;

public interface BookingService {
    // 1. Tạo đơn đặt tour mới (đã bao gồm email trích xuất từ JWT)
    BookingResponse create(CreateBookingRequest request, Long userId, String email);

    // 2. Lấy thông tin chi tiết đơn hàng
    BookingResponse get(Long id, Long requesterId, boolean privileged);

    // 3. Lấy danh sách đơn hàng cá nhân
    List<BookingResponse> getMine(Long userId);

    // 4. Hủy đơn hàng (chỉ khi CONFIRMED)
    BookingResponse cancel(Long id, Long userId);

    // 5. Lấy danh sách phòng trống theo Tour và Package
    List<AvailableRoomResponse> getAvailableRooms(UUID tourId, UUID tourPackageId);

    // 6. Gửi nhắc nhở khởi hành tự động
    int sendDepartureReminders(LocalDate departureDate);
}