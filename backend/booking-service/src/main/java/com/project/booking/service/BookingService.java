package com.project.booking.service;

import com.project.booking.dto.*;
import java.util.List;
import java.time.LocalDate;

public interface BookingService {
    BookingResponse create(CreateBookingRequest request, Long userId);
    BookingResponse get(Long id, Long requesterId, boolean privileged);
    List<BookingResponse> getMine(Long userId);
    BookingResponse cancel(Long id, Long userId);
    BookingPaymentContext getPaymentContext(Long id);
    BookingResponse confirmPayment(Long id, Long paymentId);
    int sendDepartureReminders(LocalDate departureDate);
}
