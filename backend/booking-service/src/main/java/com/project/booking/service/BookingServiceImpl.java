package com.project.booking.service;

import com.project.booking.dto.booking.*;
import com.project.booking.dto.passenger.PassengerRequest;
import com.project.booking.dto.passenger.PassengerResponse;
import com.project.booking.dto.payment.BookingPaymentContext;
import com.project.booking.dto.feedback.FeedbackEligibilityResponse;
import com.project.booking.dto.room.AvailableRoomResponse;
import com.project.booking.client.TourClient;
import com.project.booking.client.NotificationClient;
import com.project.booking.exception.BookingException;
import com.project.booking.mapper.BookingMapper;
import com.project.booking.model.Booking;
import com.project.booking.model.Passenger;
import com.project.booking.model.BookingPassenger;
import com.project.booking.model.enums.BookingStatus;
import com.project.booking.repository.BookingRepository;
import com.project.booking.repository.PassengerRepository;
import com.project.booking.repository.BookingPassengerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final BookingPassengerRepository bookingPassengerRepository;
    private final TourClient tourClient;
    private final NotificationClient notificationClient;
    private final BookingMapper bookingMapper;

    public BookingServiceImpl(BookingRepository bookingRepository,
            PassengerRepository passengerRepository,
            BookingPassengerRepository bookingPassengerRepository,
            TourClient tourClient,
            NotificationClient notificationClient,
            BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.bookingPassengerRepository = bookingPassengerRepository;
        this.tourClient = tourClient;
        this.notificationClient = notificationClient;
        this.bookingMapper = bookingMapper;
    }

    @Override
    @Transactional
    public synchronized BookingResponse create(CreateBookingRequest request, Long userId) {
        // 1. Validate tour and package info via client or business rules
        BigDecimal unitPrice = tourClient.getPackagePrice(request.getTourPackageId());
        int passengerCount = request.getPassengers().size();
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(passengerCount));

        // 2. Create and save Booking entity
        Booking booking = new Booking();
        booking.setCreatedByUserId(userId);
        booking.setTourId(request.getTourId());
        booking.setTourPackageId(request.getTourPackageId());
        booking.setBookingCode(generateBookingCode());
        booking.setNumberPassengers(passengerCount);
        booking.setPrimaryContactName(request.getPrimaryContactName().trim());
        booking.setPrimaryContactPhone(request.getPrimaryContactPhone().trim());
        booking.setTotalAmount(totalAmount);
        booking.setStatus(BookingStatus.PENDING_PAYMENT);

        Booking savedBooking = bookingRepository.save(booking);

        // 3. Save passengers and link them via BookingPassenger
        for (PassengerRequest pReq : request.getPassengers()) {
            Passenger passenger = new Passenger();
            passenger.setUserId(pReq.userId());
            passenger.setFullName(pReq.fullName().trim());
            passenger.setDateOfBirth(pReq.dateOfBirth());
            passenger.setGender(pReq.gender().trim());
            passenger.setPhoneNumber(pReq.phoneNumber());
            passenger.setEmail(pReq.email());
            passenger.setIdCardType(pReq.idCardType());
            passenger.setIdentificationNumber(pReq.identificationNumber().trim());
            passenger.setDocumentNote(pReq.documentNote());
            passenger.setIdCardImageUrl(pReq.idCardImageUrl());

            Passenger savedPassenger = passengerRepository.save(passenger);

            BookingPassenger link = new BookingPassenger();
            link.setBooking(savedBooking);
            link.setPassenger(savedPassenger);
            link.setCheckinStatus("PENDING");
            bookingPassengerRepository.save(link);
        }

        return toResponse(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse get(Long id, Long requesterId, boolean privileged) {
        Booking booking = findBookingById(id);
        if (!privileged && !Objects.equals(booking.getCreatedByUserId(), requesterId)) {
            throw new BookingException(HttpStatus.FORBIDDEN, "You cannot access this booking");
        }
        return toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getMine(Long userId) {
        return bookingRepository.findAllByCreatedByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public BookingResponse cancel(Long id, Long userId) {
        Booking booking = findBookingById(id);
        if (!Objects.equals(booking.getCreatedByUserId(), userId)) {
            throw new BookingException(HttpStatus.FORBIDDEN, "You cannot cancel this booking");
        }
        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new BookingException(HttpStatus.CONFLICT, "Only pending bookings can be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(booking);

        notificationClient.send(saved.getCreatedByUserId(), getPrimaryEmail(saved.getId()), "BOOKING_CANCELLED",
                "Booking cancelled", "Your booking #" + saved.getId() + " has been cancelled.", saved.getId());

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingPaymentContext getPaymentContext(Long id) {
        Booking booking = findBookingById(id);
        return new BookingPaymentContext(booking.getId(), booking.getCreatedByUserId(), booking.getTotalAmount(),
                booking.getStatus());
    }

    @Override
    @Transactional
    public BookingResponse confirmPayment(Long id, Long paymentId) {
        Booking booking = findBookingById(id);
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            return toResponse(booking);
        }
        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new BookingException(HttpStatus.CONFLICT, "Booking is not payable");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        Booking saved = bookingRepository.save(booking);

        notificationClient.send(saved.getCreatedByUserId(), getPrimaryEmail(saved.getId()), "PAYMENT_SUCCESS",
                "Payment successful", "Payment confirmed. Your booking code is " + saved.getBookingCode() + ".",
                saved.getId());

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getByCode(String bookingCode, Long requesterId, boolean privileged) {
        Booking booking = findBookingByCode(bookingCode);
        if (!privileged && !Objects.equals(booking.getCreatedByUserId(), requesterId)) {
            throw new BookingException(HttpStatus.FORBIDDEN, "You cannot access this booking");
        }
        return toResponse(booking);
    }

    @Override
    @Transactional
    public BookingPassengerResponse checkInPassenger(String bookingCode, Long passengerId) {
        Booking booking = findBookingByCode(bookingCode);
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BookingException(HttpStatus.CONFLICT, "Only confirmed bookings can check in");
        }

        BookingPassenger link = bookingPassengerRepository.findAllByBooking_IdOrderByIdAsc(booking.getId())
                .stream()
                .filter(bp -> bp.getPassenger().getId().equals(passengerId))
                .findFirst()
                .orElseThrow(() -> new BookingException(HttpStatus.NOT_FOUND, "Passenger not found in this booking"));

        if ("CHECKED_IN".equals(link.getCheckinStatus())) {
            throw new BookingException(HttpStatus.CONFLICT, "Passenger has already checked in");
        }

        link.setCheckinStatus("CHECKED_IN");
        link.setCheckedInAt(LocalDateTime.now());
        BookingPassenger savedLink = bookingPassengerRepository.save(link);

        return toPassengerResponse(savedLink);
    }

    @Override
    @Transactional
    public int sendDepartureReminders(LocalDate departureDate) {
        // Simplified implementation stub for reminders
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public FeedbackEligibilityResponse getFeedbackEligibility(Long bookingId, Long userId) {
        Booking booking = findBookingById(idOrDefault(bookingId));
        boolean participated = booking.getStatus() == BookingStatus.CONFIRMED;
        return new FeedbackEligibilityResponse(booking.getId(), booking.getTourId(), null, participated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailableRoomResponse> getAvailableRooms(UUID tourId, UUID tourPackageId) {
        return tourClient.getAvailableRooms(tourId, tourPackageId);
    }

    // --- HELPER METHODS ---

    private Booking findBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingException(HttpStatus.NOT_FOUND, "Booking not found: " + id));
    }

    private Booking findBookingByCode(String bookingCode) {
        String normalized = bookingCode == null ? "" : bookingCode.trim();
        return bookingRepository.findByBookingCodeIgnoreCase(normalized)
                .orElseThrow(() -> new BookingException(HttpStatus.NOT_FOUND, "Booking code not found"));
    }

    private String generateBookingCode() {
        for (int attempt = 0; attempt < 10; attempt++) {
            String code = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            if (!bookingRepository.existsByBookingCode(code)) {
                return code;
            }
        }
        throw new BookingException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot generate a unique booking code");
    }

    private String getPrimaryEmail(Long bookingId) {
        return bookingPassengerRepository.findAllByBooking_IdOrderByIdAsc(bookingId)
                .stream()
                .map(bp -> bp.getPassenger().getEmail())
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private BookingResponse toResponse(Booking b) {
        List<BookingPassengerInfoResponse> passengerInfos = bookingPassengerRepository
                .findAllByBooking_IdOrderByIdAsc(b.getId())
                .stream()
                .map(link -> new BookingPassengerInfoResponse(
                        link.getId(),
                        link.getPassenger().getFullName(),
                        link.getPassenger().getGender(),
                        link.getPassenger().getPhoneNumber(),
                        link.getPassenger().getEmail(),
                        link.getRoomId(),
                        link.getCheckinStatus(),
                        link.getCheckedInAt()))
                .toList();

        return new BookingResponse(
                b.getId(),
                b.getCreatedByUserId(),
                b.getTourId(),
                b.getTourPackageId(),
                b.getBookingCode(),
                b.getNumberPassengers(),
                b.getPrimaryContactName(),
                b.getPrimaryContactPhone(),
                b.getTotalAmount(),
                b.getStatus(),
                passengerInfos,
                b.getCreatedAt(),
                b.getUpdatedAt());
    }

    private BookingPassengerResponse toPassengerResponse(BookingPassenger link) {
        return new BookingPassengerResponse(
                link.getId(),
                link.getPassenger().getId(),
                link.getBooking().getId(),
                link.getRoomId(),
                link.getCheckinStatus(),
                link.getCheckedInAt());
    }

    private Long idOrDefault(Long id) {
        return id != null ? id : 0L;
    }
}