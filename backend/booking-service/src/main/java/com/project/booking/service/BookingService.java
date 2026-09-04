package com.project.booking.service;

import com.project.booking.dto.*;
import java.util.List;
import java.time.LocalDate;
import java.util.UUID;

public interface BookingService {
    BookingResponse create(CreateBookingRequest request, Long userId);
    BookingResponse get(Long id, Long requesterId, boolean privileged);
    List<BookingResponse> getMine(Long userId);
    BookingResponse cancel(Long id, Long userId);
    BookingPaymentContext getPaymentContext(Long id);
    BookingResponse confirmPayment(Long id, Long paymentId);
    BookingResponse getByCode(String bookingCode, Long requesterId, boolean privileged);
    PassengerVoyageResponse checkIn(String bookingCode, Long passengerVoyageId, String nfcTagId);
    PassengerVoyageResponse board(String nfcTagId);
    PassengerVoyageResponse disembark(String nfcTagId);
    int sendDepartureReminders(LocalDate departureDate);
    FeedbackEligibilityResponse getFeedbackEligibility(Long bookingId, Long userId);
    List<AvailableRoomResponse> getAvailableRooms(UUID voyageId);
}