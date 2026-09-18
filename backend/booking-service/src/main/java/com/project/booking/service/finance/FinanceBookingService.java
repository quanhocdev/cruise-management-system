package com.project.booking.service.finance;

import com.project.booking.dto.booking.BookingSummaryResponse;
import com.project.booking.dto.passenger.BookingPassengerDetailResponse;

import java.util.List;
import java.util.UUID;

public interface FinanceBookingService {
    // Lấy danh sách booking theo Tour ID để hiển thị bảng
    List<BookingSummaryResponse> getBookingsByTourId(UUID tourId);

    // Lấy danh sách hành khách kèm thông tin phòng/NFC của một Booking ID cụ thể
    List<BookingPassengerDetailResponse> getPassengersByBookingId(Long bookingId);
}