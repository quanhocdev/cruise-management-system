package com.project.booking.service;

import com.project.booking.dto.*;
import com.project.booking.client.*;
import com.project.booking.exception.BookingException;
import com.project.booking.model.Booking;
import com.project.booking.model.Passenger;
import com.project.booking.model.PassengerVoyage;
import com.project.booking.model.enums.BookingStatus;
import com.project.booking.model.enums.EmbarkationStatus;
import com.project.booking.model.enums.PassengerStatus;
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
        CreateBookingRequest request = request();
        UUID roomId = request.passengers().get(0).cabinId();
        when(tourClient.getRooms(request.voyageId())).thenReturn(List.of(room(roomId, 2)));
        when(passengerVoyageRepository.countByVoyageIdAndPassengerStatusIn(any(), any())).thenReturn(0L);
        when(passengerVoyageRepository.countByVoyageIdAndCabinIdAndPassengerStatusIn(any(), any(), any())).thenReturn(0L);
        when(passengerRepository.save(any())).thenAnswer(i -> { com.project.booking.model.Passenger p = i.getArgument(0); p.setId(2L); return p; });
        when(passengerVoyageRepository.save(any())).thenAnswer(i -> { com.project.booking.model.PassengerVoyage p = i.getArgument(0); p.setId(3L); return p; });
        when(passengerVoyageRepository.findAllByBooking_IdOrderByIdAsc(1L)).thenReturn(List.of());
        BookingResponse result = service.create(request, 7L);
        assertEquals(7L, result.createdByUserId());
        assertEquals(BookingStatus.PENDING_PAYMENT, result.status());
        assertEquals(new BigDecimal("2500000"), result.totalAmount());
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

    @Test void availableRoomsSubtractsReservedAndRegisteredPassengers() {
        UUID voyageId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();
        when(tourClient.getSchedule(voyageId)).thenReturn(new TourScheduleContext(
            voyageId, 100, java.time.LocalDate.now().plusDays(10), "OPEN"));
        when(tourClient.getRooms(voyageId)).thenReturn(List.of(new TourRoomContext(
            roomId, "A101", UUID.randomUUID(), 1, UUID.randomUUID(), "Deluxe", "Sea view",
            new BigDecimal("2500000"), 3)));
        when(passengerVoyageRepository.countByVoyageIdAndCabinIdAndPassengerStatusIn(
            eq(voyageId), eq(roomId), any())).thenReturn(2L);

        AvailableRoomResponse room = service.getAvailableRooms(voyageId).get(0);
        assertEquals(1, room.remainingCapacity());
        assertTrue(room.available());
    }

    @Test void createRejectsRoomWithoutEnoughCapacity() {
        CreateBookingRequest request = request();
        UUID roomId = request.passengers().get(0).cabinId();
        when(tourClient.getSchedule(request.voyageId())).thenReturn(new TourScheduleContext(
            request.voyageId(), 100, java.time.LocalDate.now().plusDays(10), "OPEN"));
        when(tourClient.getRooms(request.voyageId())).thenReturn(List.of(room(roomId, 1)));
        when(passengerVoyageRepository.countByVoyageIdAndCabinIdAndPassengerStatusIn(
            eq(request.voyageId()), eq(roomId), any())).thenReturn(1L);

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

    @Test void checkInAssignsNfcAndRejectsSecondCheckIn() {
        Booking booking = booking(BookingStatus.CONFIRMED, 10L); booking.setBookingCode("CR00000001");
        PassengerVoyage link = passengerVoyage(booking, EmbarkationStatus.NOT_CHECKED_IN);
        when(repository.findByBookingCodeIgnoreCase("CR00000001")).thenReturn(Optional.of(booking));
        when(passengerVoyageRepository.findById(3L)).thenReturn(Optional.of(link));
        when(passengerVoyageRepository.existsByNfcTagIdIgnoreCase("TAG-01")).thenReturn(false);
        when(passengerVoyageRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        PassengerVoyageResponse result = service.checkIn("CR00000001", 3L, "tag-01");
        assertEquals(EmbarkationStatus.CHECKED_IN, result.embarkationStatus());
        assertEquals("TAG-01", result.nfcTagId());
        assertThrows(BookingException.class, () -> service.checkIn("CR00000001", 3L, "tag-02"));
    }

    @Test void nfcLifecycleRequiresCorrectOrder() {
        Booking booking = booking(BookingStatus.CONFIRMED, 10L);
        PassengerVoyage link = passengerVoyage(booking, EmbarkationStatus.CHECKED_IN); link.setNfcTagId("TAG-01");
        when(passengerVoyageRepository.findByNfcTagIdIgnoreCase("TAG-01")).thenReturn(Optional.of(link));
        when(passengerVoyageRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        assertEquals(EmbarkationStatus.BOARDED, service.board("tag-01").embarkationStatus());
        assertEquals(EmbarkationStatus.DISEMBARKED, service.disembark("tag-01").embarkationStatus());
        assertThrows(BookingException.class, () -> service.board("tag-01"));
    }

    @Test void departureReminderIsSentOnlyOnceAfterSuccessfulDelivery() {
        java.time.LocalDate tomorrow = java.time.LocalDate.now().plusDays(1);
        Booking booking = booking(BookingStatus.CONFIRMED, 10L); booking.setBookingCode("CR00000001");
        booking.setVoyageStartDate(tomorrow);
        when(repository.findAllByStatusAndVoyageStartDateAndDepartureReminderSentAtIsNull(BookingStatus.CONFIRMED, tomorrow))
            .thenReturn(List.of(booking));
        when(passengerVoyageRepository.findAllByBooking_IdOrderByIdAsc(1L)).thenReturn(List.of());
        when(notificationClient.send(eq(7L), isNull(), eq("DEPARTURE_REMINDER"), anyString(), anyString(), eq(1L)))
            .thenReturn(true);
        assertEquals(1, service.sendDepartureReminders(tomorrow));
        assertNotNull(booking.getDepartureReminderSentAt()); verify(repository).save(booking);
    }

    @Test void disembarkedPassengerIsEligibleForFeedback() {
        Booking booking = booking(BookingStatus.CONFIRMED, 10L);
        PassengerVoyage link = passengerVoyage(booking, EmbarkationStatus.DISEMBARKED);
        link.getPassenger().setUserId(7L);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        when(passengerVoyageRepository.findFirstByBooking_IdAndPassenger_UserId(1L, 7L)).thenReturn(Optional.of(link));
        FeedbackEligibilityResponse result = service.getFeedbackEligibility(1L, 7L);
        assertTrue(result.participated()); assertEquals(3L, result.passengerVoyageId());
    }

    @Test void bookingOwnerWhoIsNotPassengerIsNotEligibleForFeedback() {
        Booking booking = booking(BookingStatus.CONFIRMED, 10L);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        when(passengerVoyageRepository.findFirstByBooking_IdAndPassenger_UserId(1L, 7L)).thenReturn(Optional.empty());
        assertFalse(service.getFeedbackEligibility(1L, 7L).participated());
    }

    private CreateBookingRequest request() {
        return new CreateBookingRequest(UUID.randomUUID(), "Nguyen Van A", "0900000000",
            List.of(new CreatePassengerRequest(null, "Nguyen Van A",
            java.time.LocalDate.of(1990, 1, 1), "MALE", "0900000000", "a@example.com", UUID.randomUUID())));
    }
    private TourRoomContext room(UUID roomId, int capacity) {
        return new TourRoomContext(roomId, "A101", UUID.randomUUID(), 1, UUID.randomUUID(),
            "Deluxe", "Sea view", new BigDecimal("2500000"), capacity);
    }
    private Booking booking(BookingStatus status, Long paymentId) {
        Booking b = new Booking(); b.setId(1L); b.setCreatedByUserId(7L); b.setVoyageId(UUID.randomUUID());
        b.setPrimaryContactName("Nguyen Van A"); b.setPrimaryContactPhone("0900000000");
        b.setTotalAmount(new BigDecimal("1000000"));
        b.setStatus(status); b.setPaymentId(paymentId); b.setCreatedAt(Instant.now()); b.setUpdatedAt(Instant.now());
        return b;
    }
    private PassengerVoyage passengerVoyage(Booking booking, EmbarkationStatus status) {
        Passenger passenger = new Passenger(); passenger.setId(2L); passenger.setFullName("Nguyen Van A");
        PassengerVoyage link = new PassengerVoyage(); link.setId(3L); link.setPassenger(passenger);
        link.setBooking(booking); link.setVoyageId(booking.getVoyageId());
        link.setPassengerStatus(PassengerStatus.REGISTERED); link.setEmbarkationStatus(status);
        return link;
    }
}
