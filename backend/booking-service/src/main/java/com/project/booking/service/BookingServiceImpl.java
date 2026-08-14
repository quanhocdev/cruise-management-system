package com.project.booking.service;

import com.project.booking.dto.*;
import com.project.booking.client.*;
import com.project.booking.exception.BookingException;
import com.project.booking.model.Booking;
import com.project.booking.model.Passenger;
import com.project.booking.model.PassengerVoyage;
import com.project.booking.model.enums.*;
import com.project.booking.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final PassengerRepository passengerRepository;
    private final PassengerVoyageRepository passengerVoyageRepository;
    private final TourClient tourClient;
    public BookingServiceImpl(BookingRepository repository, PassengerRepository passengerRepository,
                              PassengerVoyageRepository passengerVoyageRepository, TourClient tourClient) {
        this.repository = repository; this.passengerRepository = passengerRepository;
        this.passengerVoyageRepository = passengerVoyageRepository; this.tourClient = tourClient;
    }

    @Override @Transactional
    public synchronized BookingResponse create(CreateBookingRequest request, Long userId) {
        TourScheduleContext voyage = tourClient.getSchedule(request.voyageId());
        validateAvailability(request, voyage);
        Instant now = Instant.now();
        Booking booking = new Booking();
        booking.setCreatedByUserId(userId); booking.setVoyageId(request.voyageId());
        booking.setPrimaryContactName(request.primaryContactName().trim());
        booking.setPrimaryContactPhone(request.primaryContactPhone().trim());
        booking.setTotalAmount(request.totalAmount());
        booking.setStatus(BookingStatus.PENDING_PAYMENT); booking.setCreatedAt(now); booking.setUpdatedAt(now);
        Booking saved = repository.save(booking);
        for (CreatePassengerRequest item : request.passengers()) {
            Passenger passenger = new Passenger(); passenger.setUserId(item.userId());
            passenger.setFullName(item.fullName().trim()); passenger.setDateOfBirth(item.dateOfBirth());
            passenger.setGender(item.gender().trim()); passenger.setPhoneNumber(item.phoneNumber()); passenger.setEmail(item.email());
            passenger = passengerRepository.save(passenger);
            PassengerVoyage link = new PassengerVoyage(); link.setPassenger(passenger); link.setBooking(saved);
            link.setVoyageId(saved.getVoyageId()); link.setCabinId(item.cabinId());
            link.setPassengerStatus(PassengerStatus.RESERVED); link.setEmbarkationStatus(EmbarkationStatus.NOT_CHECKED_IN);
            passengerVoyageRepository.save(link);
        }
        return toResponse(saved);
    }

    @Override @Transactional(readOnly = true)
    public BookingResponse get(Long id, Long requesterId, boolean privileged) {
        Booking booking = find(id);
        if (!privileged && !Objects.equals(booking.getCreatedByUserId(), requesterId))
            throw new BookingException(HttpStatus.FORBIDDEN, "You cannot access this booking");
        return toResponse(booking);
    }

    @Override @Transactional(readOnly = true)
    public List<BookingResponse> getMine(Long userId) {
        return repository.findAllByCreatedByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    @Override @Transactional
    public BookingResponse cancel(Long id, Long userId) {
        Booking booking = find(id);
        if (!Objects.equals(booking.getCreatedByUserId(), userId))
            throw new BookingException(HttpStatus.FORBIDDEN, "You cannot cancel this booking");
        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT)
            throw new BookingException(HttpStatus.CONFLICT, "Only a pending booking can be cancelled");
        booking.setStatus(BookingStatus.CANCELLED); booking.setUpdatedAt(Instant.now());
        passengerVoyageRepository.findAllByBooking_IdOrderByIdAsc(id).forEach(link -> {
            link.setPassengerStatus(PassengerStatus.CANCELLED); passengerVoyageRepository.save(link);
        });
        return toResponse(repository.save(booking));
    }

    @Override @Transactional(readOnly = true)
    public BookingPaymentContext getPaymentContext(Long id) {
        Booking booking = find(id);
        return new BookingPaymentContext(booking.getId(), booking.getCreatedByUserId(), booking.getTotalAmount(), booking.getStatus());
    }

    @Override @Transactional
    public BookingResponse confirmPayment(Long id, Long paymentId) {
        Booking booking = find(id);
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            if (paymentId.equals(booking.getPaymentId())) return toResponse(booking);
            throw new BookingException(HttpStatus.CONFLICT, "Booking was confirmed by another payment");
        }
        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT)
            throw new BookingException(HttpStatus.CONFLICT, "Booking is not payable");
        booking.setStatus(BookingStatus.CONFIRMED); booking.setPaymentId(paymentId);
        booking.setBookingCode(generateBookingCode(booking.getId())); booking.setUpdatedAt(Instant.now());
        passengerVoyageRepository.findAllByBooking_IdOrderByIdAsc(id).forEach(link -> {
            link.setPassengerStatus(PassengerStatus.REGISTERED); passengerVoyageRepository.save(link);
        });
        return toResponse(repository.save(booking));
    }

    private Booking find(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new BookingException(HttpStatus.NOT_FOUND, "Booking not found: " + id));
    }
    private BookingResponse toResponse(Booking b) {
        List<PassengerVoyageResponse> passengers = passengerVoyageRepository
            .findAllByBooking_IdOrderByIdAsc(b.getId()).stream().map(link -> {
                Passenger p = link.getPassenger();
                return new PassengerVoyageResponse(link.getId(), p.getId(), p.getUserId(), p.getFullName(),
                    p.getDateOfBirth(), p.getGender(), p.getPhoneNumber(), p.getEmail(), link.getCabinId(),
                    link.getPassengerStatus(), link.getEmbarkationStatus());
            }).toList();
        return new BookingResponse(b.getId(), b.getVoyageId(), b.getBookingCode(), b.getCreatedByUserId(),
            b.getPrimaryContactName(), b.getPrimaryContactPhone(), b.getTotalAmount(), b.getStatus(), b.getPaymentId(),
            b.getCreatedAt(), b.getUpdatedAt(), passengers);
    }
    private String generateBookingCode(Long bookingId) {
        for (int attempt = 0; attempt < 10; attempt++) {
            String code = "CR" + String.format("%08d", bookingId) + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            if (!repository.existsByBookingCode(code)) return code;
        }
        throw new BookingException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot generate a unique booking code");
    }
    private void validateAvailability(CreateBookingRequest request, TourScheduleContext voyage) {
        if (!request.voyageId().equals(voyage.voyageId()))
            throw new BookingException(HttpStatus.BAD_REQUEST, "Voyage reference does not match");
        if (!"OPEN".equals(voyage.status()))
            throw new BookingException(HttpStatus.CONFLICT, "Voyage is not open for booking");
        if (!voyage.startDate().isAfter(java.time.LocalDate.now()))
            throw new BookingException(HttpStatus.CONFLICT, "Voyage registration has closed");
        long occupied = passengerVoyageRepository.countByVoyageIdAndPassengerStatusIn(
            request.voyageId(), List.of(PassengerStatus.RESERVED, PassengerStatus.REGISTERED));
        if (occupied + request.passengers().size() > voyage.capacity())
            throw new BookingException(HttpStatus.CONFLICT, "Voyage does not have enough available capacity");
    }
}
