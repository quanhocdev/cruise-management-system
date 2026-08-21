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
    private final NotificationClient notificationClient;
    public BookingServiceImpl(BookingRepository repository, PassengerRepository passengerRepository,
                              PassengerVoyageRepository passengerVoyageRepository, TourClient tourClient,
                              NotificationClient notificationClient) {
        this.repository = repository; this.passengerRepository = passengerRepository;
        this.passengerVoyageRepository = passengerVoyageRepository; this.tourClient = tourClient;
        this.notificationClient = notificationClient;
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
        booking.setVoyageStartDate(voyage.startDate());
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
        Booking saved = repository.save(booking);
        notificationClient.send(saved.getCreatedByUserId(), firstEmail(saved), "BOOKING_CANCELLED",
            "Booking cancelled", "Your booking #" + saved.getId() + " has been cancelled.", saved.getId());
        return toResponse(saved);
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
        Booking saved = repository.save(booking);
        notificationClient.send(saved.getCreatedByUserId(), firstEmail(saved), "PAYMENT_SUCCESS",
            "Payment successful", "Payment confirmed. Your booking code is " + saved.getBookingCode() + ".",
            saved.getId());
        return toResponse(saved);
    }

    @Override @Transactional(readOnly = true)
    public BookingResponse getByCode(String bookingCode, Long requesterId, boolean privileged) {
        Booking booking = findByCode(bookingCode);
        if (!privileged && !Objects.equals(booking.getCreatedByUserId(), requesterId))
            throw new BookingException(HttpStatus.FORBIDDEN, "You cannot access this booking");
        return toResponse(booking);
    }

    @Override @Transactional
    public PassengerVoyageResponse checkIn(String bookingCode, Long passengerVoyageId, String nfcTagId) {
        Booking booking = findByCode(bookingCode);
        if (booking.getStatus() != BookingStatus.CONFIRMED)
            throw new BookingException(HttpStatus.CONFLICT, "Only a confirmed booking can be checked in");
        PassengerVoyage link = findPassengerVoyage(passengerVoyageId);
        if (!Objects.equals(link.getBooking().getId(), booking.getId()))
            throw new BookingException(HttpStatus.BAD_REQUEST, "Passenger does not belong to this booking");
        if (link.getPassengerStatus() != PassengerStatus.REGISTERED)
            throw new BookingException(HttpStatus.CONFLICT, "Passenger is not registered for this voyage");
        if (link.getEmbarkationStatus() != EmbarkationStatus.NOT_CHECKED_IN)
            throw new BookingException(HttpStatus.CONFLICT, "Passenger has already been checked in");
        String normalizedTag = normalizeTag(nfcTagId);
        if (passengerVoyageRepository.existsByNfcTagIdIgnoreCase(normalizedTag))
            throw new BookingException(HttpStatus.CONFLICT, "NFC tag is already assigned");
        link.setNfcTagId(normalizedTag);
        link.setEmbarkationStatus(EmbarkationStatus.CHECKED_IN);
        link.setCheckedInAt(Instant.now());
        PassengerVoyage saved = passengerVoyageRepository.save(link);
        notificationClient.send(booking.getCreatedByUserId(), saved.getPassenger().getEmail(), "CHECK_IN_SUCCESS",
            "Check-in successful", saved.getPassenger().getFullName() + " has checked in successfully.", booking.getId());
        return toPassengerResponse(saved);
    }

    @Override @Transactional
    public PassengerVoyageResponse board(String nfcTagId) {
        PassengerVoyage link = findByNfc(nfcTagId);
        if (link.getEmbarkationStatus() != EmbarkationStatus.CHECKED_IN)
            throw new BookingException(HttpStatus.CONFLICT, "Passenger must be checked in before boarding");
        link.setEmbarkationStatus(EmbarkationStatus.BOARDED);
        link.setBoardedAt(Instant.now());
        return toPassengerResponse(passengerVoyageRepository.save(link));
    }

    @Override @Transactional
    public PassengerVoyageResponse disembark(String nfcTagId) {
        PassengerVoyage link = findByNfc(nfcTagId);
        if (link.getEmbarkationStatus() != EmbarkationStatus.BOARDED)
            throw new BookingException(HttpStatus.CONFLICT, "Passenger must be on board before disembarking");
        link.setEmbarkationStatus(EmbarkationStatus.DISEMBARKED);
        link.setDisembarkedAt(Instant.now());
        return toPassengerResponse(passengerVoyageRepository.save(link));
    }

    @Override @Transactional
    public int sendDepartureReminders(java.time.LocalDate departureDate) {
        int sent = 0;
        for (Booking booking : repository.findAllByStatusAndVoyageStartDateAndDepartureReminderSentAtIsNull(
                BookingStatus.CONFIRMED, departureDate)) {
            boolean delivered = notificationClient.send(booking.getCreatedByUserId(), firstEmail(booking),
                "DEPARTURE_REMINDER", "Upcoming cruise departure",
                "Your cruise departs on " + departureDate + ". Booking code: " + booking.getBookingCode() + ".",
                booking.getId());
            if (delivered) {
                booking.setDepartureReminderSentAt(Instant.now()); repository.save(booking); sent++;
            }
        }
        return sent;
    }

    @Override @Transactional(readOnly = true)
    public FeedbackEligibilityResponse getFeedbackEligibility(Long bookingId, Long userId) {
        Booking booking = find(bookingId);
        PassengerVoyage passengerVoyage = passengerVoyageRepository
            .findFirstByBooking_IdAndPassenger_UserId(bookingId, userId).orElse(null);
        if (passengerVoyage == null)
            return new FeedbackEligibilityResponse(booking.getId(), booking.getVoyageId(), null, false);
        boolean participated = booking.getStatus() == BookingStatus.CONFIRMED
            && passengerVoyage.getPassengerStatus() == PassengerStatus.REGISTERED
            && passengerVoyage.getEmbarkationStatus() == EmbarkationStatus.DISEMBARKED;
        return new FeedbackEligibilityResponse(booking.getId(), booking.getVoyageId(),
            passengerVoyage.getId(), participated);
    }

    private Booking find(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new BookingException(HttpStatus.NOT_FOUND, "Booking not found: " + id));
    }
    private Booking findByCode(String bookingCode) {
        String normalized = bookingCode == null ? "" : bookingCode.trim();
        return repository.findByBookingCodeIgnoreCase(normalized)
            .orElseThrow(() -> new BookingException(HttpStatus.NOT_FOUND, "Booking code not found"));
    }
    private PassengerVoyage findPassengerVoyage(Long id) {
        return passengerVoyageRepository.findById(id)
            .orElseThrow(() -> new BookingException(HttpStatus.NOT_FOUND, "Passenger voyage not found: " + id));
    }
    private PassengerVoyage findByNfc(String nfcTagId) {
        return passengerVoyageRepository.findByNfcTagIdIgnoreCase(normalizeTag(nfcTagId))
            .orElseThrow(() -> new BookingException(HttpStatus.NOT_FOUND, "NFC tag is not assigned"));
    }
    private String normalizeTag(String nfcTagId) {
        if (nfcTagId == null || nfcTagId.isBlank())
            throw new BookingException(HttpStatus.BAD_REQUEST, "NFC tag ID is required");
        return nfcTagId.trim().toUpperCase(java.util.Locale.ROOT);
    }
    private String firstEmail(Booking booking) {
        return passengerVoyageRepository.findAllByBooking_IdOrderByIdAsc(booking.getId()).stream()
            .map(PassengerVoyage::getPassenger).map(Passenger::getEmail)
            .filter(Objects::nonNull).filter(email -> !email.isBlank()).findFirst().orElse(null);
    }
    private BookingResponse toResponse(Booking b) {
        List<PassengerVoyageResponse> passengers = passengerVoyageRepository
            .findAllByBooking_IdOrderByIdAsc(b.getId()).stream().map(link -> {
                return toPassengerResponse(link);
            }).toList();
        return new BookingResponse(b.getId(), b.getVoyageId(), b.getBookingCode(), b.getCreatedByUserId(),
            b.getPrimaryContactName(), b.getPrimaryContactPhone(), b.getTotalAmount(), b.getStatus(), b.getPaymentId(),
            b.getCreatedAt(), b.getUpdatedAt(), passengers);
    }
    private PassengerVoyageResponse toPassengerResponse(PassengerVoyage link) {
        Passenger p = link.getPassenger();
        return new PassengerVoyageResponse(link.getId(), p.getId(), p.getUserId(), p.getFullName(),
            p.getDateOfBirth(), p.getGender(), p.getPhoneNumber(), p.getEmail(), link.getCabinId(),
            link.getPassengerStatus(), link.getEmbarkationStatus(), link.getNfcTagId(),
            link.getCheckedInAt(), link.getBoardedAt(), link.getDisembarkedAt());
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
