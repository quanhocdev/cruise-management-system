package com.project.booking.mapper;

import com.project.booking.client.TourClient;
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

    public BookingResponse toResponse(
            Booking booking,
            List<BookingPassenger> passengers,
            TourClient.TourInfo tourInfo) {

        List<BookingPassengerInfoResponse> passengerInfos = passengers.stream()
                .map(this::toPassengerInfoResponse)
                .toList();

        return new BookingResponse(
                booking.getId(),
                booking.getCreatedByUserId(),

                // Thông tin Tour
                booking.getTourId(),
                tourInfo != null ? tourInfo.tourCode() : null,
                tourInfo != null ? tourInfo.tourName() : null,
                tourInfo != null ? tourInfo.departureDate() : null,
                tourInfo != null ? tourInfo.endDate() : null,

                // Thông tin gói Tour
                booking.getTourPackageId(),

                // Thông tin Booking
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

    public BookingPassengerResponse toPassengerResponse(BookingPassenger link) {
        return new BookingPassengerResponse(
                link.getId(),
                link.getPassenger().getId(),
                link.getBooking().getId(),
                link.getRoomId(),
                link.getStatus() != null ? link.getStatus().name() : null,
                link.getCheckedInAt());
    }

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

    public BookingPassengerDetailResponse toPassengerDetailResponse(BookingPassenger link) {
        if (link == null) {
            return null;
        }

        var p = link.getPassenger();
        var booking = link.getBooking();

        return new BookingPassengerDetailResponse(
                booking != null ? booking.getTourPackageId() : null,
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