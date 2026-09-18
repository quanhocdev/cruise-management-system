package com.project.booking.mapper;

import com.project.booking.dto.booking.BookingPassengerInfoResponse;
import com.project.booking.dto.booking.BookingPassengerResponse;
import com.project.booking.dto.booking.BookingResponse;
import com.project.booking.dto.booking.BookingSummaryResponse;
import com.project.booking.dto.passenger.BookingPassengerDetailResponse;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class BookingMapper {

    // Chuyển đổi Booking Entity + danh sách BookingPassenger sang BookingResponse
    public BookingResponse toResponse(Booking booking, List<BookingPassenger> passengers) {
        List<BookingPassengerInfoResponse> passengerInfos = passengers.stream()
                .map(this::toPassengerInfoResponse)
                .toList();

        return new BookingResponse(
                booking.getId(),
                booking.getCreatedByUserId(),
                booking.getTourId(),
                booking.getTourPackageId(),
                booking.getBookingCode(),
                booking.getNumberPassengers(),
                booking.getPrimaryContactName(),
                booking.getPrimaryContactPhone(),
                booking.getTotalAmount(),
                booking.getStatus(),
                passengerInfos,
                booking.getCreatedAt(),
                booking.getUpdatedAt());
    }

    // Chuyển đổi thông tin hiển thị ngắn gọn cho từng hành khách trong đơn
    public BookingPassengerInfoResponse toPassengerInfoResponse(BookingPassenger link) {
        var p = link.getPassenger();
        return new BookingPassengerInfoResponse(
                link.getId(),
                p.getFullName(),
                p.getGender(),
                p.getPhoneNumber(),
                p.getEmail(),
                link.getRoomId(),
                link.getStatus() != null ? link.getStatus().name() : null,
                link.getCheckedInAt());
    }

    // Chuyển đổi kết quả check-in chi tiết cho từng vé hành khách
    public BookingPassengerResponse toPassengerResponse(BookingPassenger link) {
        return new BookingPassengerResponse(
                link.getId(),
                link.getPassenger().getId(),
                link.getBooking().getId(),
                link.getRoomId(),
                link.getStatus() != null ? link.getStatus().name() : null,
                link.getCheckedInAt());
    }

    // ==========================================
    // BỔ SUNG CHO PHẦN FINANCE / QUẢN TRỊ TOUR
    // ==========================================

    // 1. Map Booking Entity sang BookingSummaryResponse (Dùng cho bảng danh sách
    // booking theo tour)
    public BookingSummaryResponse toSummaryResponse(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingSummaryResponse(
                booking.getId(),
                booking.getTourId(),
                booking.getBookingCode(),
                booking.getPrimaryContactName(),
                booking.getPrimaryContactPhone(),
                booking.getPrimaryContactEmail(),
                booking.getNumberPassengers(),
                booking.getNumberOfRooms(),
                booking.getTotalAmount(),
                booking.getStatus(),
                booking.getCreatedAt());
    }

    // 2. Map BookingPassenger Entity sang BookingPassengerDetailResponse (Dùng cho
    // trang chi tiết hành khách)
    public BookingPassengerDetailResponse toPassengerDetailResponse(BookingPassenger link) {
        if (link == null) {
            return null;
        }
        var p = link.getPassenger();
        return new BookingPassengerDetailResponse(
                link.getId(),
                link.getRoomId(),
                link.getStatus(),
                link.getCheckedInAt(),
                link.getCheckedOutAt(),
                link.getNfcCardUid(),
                p != null ? p.getId() : null,
                p != null ? p.getFullName() : null,
                p != null ? p.getDateOfBirth() : null,
                p != null ? p.getGender() : null,
                p != null ? p.getPhoneNumber() : null,
                p != null ? p.getEmail() : null,
                p != null ? p.getIdCardType() : null,
                p != null ? p.getIdentificationNumber() : null,
                p != null ? p.getDocumentNote() : null,
                p != null ? p.getIdCardImageUrl() : null);
    }
}