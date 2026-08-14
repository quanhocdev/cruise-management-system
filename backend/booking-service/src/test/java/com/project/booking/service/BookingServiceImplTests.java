package com.project.booking.service;

import com.project.booking.dto.*;
import com.project.booking.exception.BookingException;
import com.project.booking.model.Booking;
import com.project.booking.model.enums.BookingStatus;
import com.project.booking.repository.BookingRepository;
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
    BookingServiceImpl service;
    @BeforeEach void setUp() { service = new BookingServiceImpl(repository); }

    @Test void createUsesAuthenticatedUserAndPendingStatus() {
        when(repository.save(any())).thenAnswer(i -> { Booking b = i.getArgument(0); b.setId(1L); return b; });
        BookingResponse result = service.create(request(), 7L);
        assertEquals(7L, result.userId());
        assertEquals(BookingStatus.PENDING_PAYMENT, result.status());
    }

    @Test void ownerCheckRejectsAnotherUser() {
        when(repository.findById(1L)).thenReturn(Optional.of(booking(BookingStatus.PENDING_PAYMENT, null)));
        assertThrows(BookingException.class, () -> service.get(1L, 99L, false));
    }

    @Test void confirmingSamePaymentIsIdempotent() {
        Booking booking = booking(BookingStatus.CONFIRMED, 10L);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        BookingResponse result = service.confirmPayment(1L, 10L);
        assertEquals(BookingStatus.CONFIRMED, result.status());
        verify(repository, never()).save(any());
    }

    @Test void cancelledBookingCannotBePaid() {
        when(repository.findById(1L)).thenReturn(Optional.of(booking(BookingStatus.CANCELLED, null)));
        assertThrows(BookingException.class, () -> service.confirmPayment(1L, 10L));
    }

    private CreateBookingRequest request() {
        return new CreateBookingRequest(UUID.randomUUID(), UUID.randomUUID(), 2, new BigDecimal("1000000"));
    }
    private Booking booking(BookingStatus status, Long paymentId) {
        Booking b = new Booking(); b.setId(1L); b.setUserId(7L); b.setScheduleId(UUID.randomUUID());
        b.setRoomId(UUID.randomUUID()); b.setGuestCount(2); b.setTotalAmount(new BigDecimal("1000000"));
        b.setStatus(status); b.setPaymentId(paymentId); b.setCreatedAt(Instant.now()); b.setUpdatedAt(Instant.now());
        return b;
    }
}
