package com.project.chat.service;

import com.project.chat.dto.ConfirmedBookingResponse;
import com.project.chat.model.ConfirmedBooking;
import com.project.chat.repository.ConfirmedBookingRepository;
import com.project.common.event.BookingConfirmedEvent;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.time.Instant;

@Service
public class ConfirmedBookingService {

    private final ConfirmedBookingRepository confirmedBookingRepository;

    public ConfirmedBookingService(
            ConfirmedBookingRepository confirmedBookingRepository) {
        this.confirmedBookingRepository = confirmedBookingRepository;
    }

    @Transactional
    public void saveConfirmedBooking(BookingConfirmedEvent event) {

        if (event.bookingId() == null || event.recipientUserId() == null) {
            throw new IllegalArgumentException(
                    "Booking ID và Passenger ID không được để trống.");
        }

        ConfirmedBooking booking = confirmedBookingRepository
                .findById(event.bookingId())
                .orElseGet(ConfirmedBooking::new);

        booking.setBookingId(event.bookingId());
        booking.setPrimaryHolderUserId(event.recipientUserId());
        booking.setBookingCode(event.bookingCode());
        booking.setRecipientEmail(event.recipientEmail());
        booking.setRecipientName(event.recipientName());
        booking.setNumberPassengers(event.numberPassengers());
        booking.setTotalAmount(event.totalAmount());

        if (booking.getConfirmedAt() == null) {
            booking.setConfirmedAt(Instant.now());
        }

        confirmedBookingRepository.save(booking);
    }
    @Transactional(readOnly = true)
public List<ConfirmedBookingResponse> getMyConfirmedBookings(UUID userId) {
    return confirmedBookingRepository
            .findByPrimaryHolderUserIdOrderByConfirmedAtDesc(userId)
            .stream()
            .map(booking -> new ConfirmedBookingResponse(
                    booking.getBookingId(),
                    booking.getBookingCode(),
                    booking.getRecipientName(),
                    booking.getNumberPassengers(),
                    booking.getTotalAmount(),
                    booking.getConfirmedAt()
            ))
            .toList();
}
}