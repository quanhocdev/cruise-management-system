package com.project.booking.service;

import com.project.booking.dto.*;
import com.project.booking.client.*;
import com.project.booking.exception.BookingException;
import com.project.booking.model.Booking;
import com.project.booking.model.enums.BookingStatus;
import com.project.booking.repository.BookingRepository;
import com.project.booking.repository.PassengerRepository;
import com.project.booking.repository.PassengerVoyageRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTests {
    @Mock BookingRepository repository;
    @Mock PassengerRepository passengerRepository;
    @Mock PassengerVoyageRepository passengerVoyageRepository;
    @Mock TourClient tourClient;
    @Mock NotificationClient notificationClient;
    BookingServiceImpl service;
    @BeforeEach void setUp() {
        service = new BookingServiceImpl(repository, passengerRepository, passengerVoyageRepository, tourClient, notificationClient);
    }

    @Test void createUsesAuthenticatedUserAndPendingStatus() {
        when(repository.save(any())).thenAnswer(i -> { Booking b = i.getArgument(0); b.setId(1L); return b; });
        when(tourClient.getSchedule(any())).thenAnswer(i -> new TourScheduleContext(
            i.getArgument(0), 100, java.time.LocalDate.now().plusDays(10), "OPEN"));
        when(passengerVoyageRepository.countByVoyageIdAndPassengerStatusIn(any(), any())).thenReturn(0L);
        when(passengerRepository.save(any())).thenAnswer(i -> { com.project.booking.model.Passenger p = i.getArgument(0); p.setId(2L); return p; });
        when(passengerVoyageRepository.save(any())).thenAnswer(i -> { com.project.booking.model.PassengerVoyage p = i.getArgument(0); p.setId(3L); return p; });
        when(passengerVoyageRepository.findAllByBooking_IdOrderByIdAsc(1L)).thenReturn(List.of());
        BookingResponse result = service.create(request(), 7L);
        assertEquals(7L, result.createdByUserId());
        assertEquals(BookingStatus.PENDING_PAYMENT, result.status());
    }

    @Test void ownerCheckRejectsAnotherUser() {
        when(repository.findById(1L)).thenReturn(Optional.of(booking(BookingStatus.PENDING_PAYMENT, null)));
        assertThrows(BookingException.class, () -> service.get(1L, 99L, false));
    }

    @Test void createRejectsVoyageWithoutEnoughCapacity() {
        CreateBookingRequest request = request();
        when(tourClient.getSchedule(request.voyageId())).thenReturn(new TourScheduleContext(
            request.voyageId(), 1, java.time.LocalDate.now().plusDays(10), "OPEN"));
        when(passengerVoyageRepository.countByVoyageIdAndPassengerStatusIn(any(), any())).thenReturn(1L);
        assertThrows(BookingException.class, () -> service.create(request, 7L));
    }

    @Test void confirmingSamePaymentIsIdempotent() {
        Booking booking = booking(BookingStatus.CONFIRMED, 10L);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        BookingResponse result = service.confirmPayment(1L, 10L);
        assertEquals(BookingStatus.CONFIRMED, result.status());
        verify(repository, never()).save(any());
    }

    @Test void successfulPaymentGeneratesBookingCode() {
        Booking booking = booking(BookingStatus.PENDING_PAYMENT, null);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        when(repository.existsByBookingCode(anyString())).thenReturn(false);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(passengerVoyageRepository.findAllByBooking_IdOrderByIdAsc(1L)).thenReturn(List.of());
        BookingResponse result = service.confirmPayment(1L, 10L);
        assertEquals(BookingStatus.CONFIRMED, result.status());
        assertNotNull(result.bookingCode());
        verify(notificationClient).send(eq(7L), isNull(), eq("PAYMENT_SUCCESS"),
            anyString(), contains(result.bookingCode()), eq(1L));
    }

    @Test void cancelledBookingCannotBePaid() {
        when(repository.findById(1L)).thenReturn(Optional.of(booking(BookingStatus.CANCELLED, null)));
        assertThrows(BookingException.class, () -> service.confirmPayment(1L, 10L));
    }

    private CreateBookingRequest request() {
        return new CreateBookingRequest(UUID.randomUUID(), "Nguyen Van A", "0900000000",
            new BigDecimal("1000000"), List.of(new CreatePassengerRequest(null, "Nguyen Van A",
            java.time.LocalDate.of(1990, 1, 1), "MALE", "0900000000", "a@example.com", null)));
    }
    private Booking booking(BookingStatus status, Long paymentId) {
        Booking b = new Booking(); b.setId(1L); b.setCreatedByUserId(7L); b.setVoyageId(UUID.randomUUID());
        b.setPrimaryContactName("Nguyen Van A"); b.setPrimaryContactPhone("0900000000");
        b.setTotalAmount(new BigDecimal("1000000"));
        b.setStatus(status); b.setPaymentId(paymentId); b.setCreatedAt(Instant.now()); b.setUpdatedAt(Instant.now());
        return b;
    }
}
