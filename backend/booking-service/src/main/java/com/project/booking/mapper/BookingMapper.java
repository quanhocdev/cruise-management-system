package com.project.booking.mapper;

import com.project.booking.dto.booking.BookingPassengerInfoResponse;
import com.project.booking.dto.booking.BookingPassengerResponse;
import com.project.booking.dto.booking.BookingResponse;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class BookingMapper {

    // Chuyển đổi Booking Entity + danh sách BookingPassenger sang BookingResponse
    // Record
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
                link.getCheckinStatus(),
                link.getCheckedInAt());
    }

    // Chuyển đổi kết quả check-in chi tiết cho từng vé hành khách
    public BookingPassengerResponse toPassengerResponse(BookingPassenger link) {
        return new BookingPassengerResponse(
                link.getId(),
                link.getPassenger().getId(),
                link.getBooking().getId(),
                link.getRoomId(),
                link.getCheckinStatus(),
                link.getCheckedInAt());
    }
}