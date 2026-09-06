package com.project.booking.service;

import com.project.booking.dto.booking.*;
import com.project.booking.dto.passenger.PassengerRequest;
import com.project.booking.dto.AvailableRoomResponse;
import com.project.booking.exception.AppException;
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
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final BookingPassengerRepository bookingPassengerRepository;
    private final BookingMapper bookingMapper;

    public BookingServiceImpl(BookingRepository bookingRepository,
            PassengerRepository passengerRepository,
            BookingPassengerRepository bookingPassengerRepository,
            BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.bookingPassengerRepository = bookingPassengerRepository;
        this.bookingMapper = bookingMapper;
    }

    @Override
    @Transactional
    public synchronized BookingResponse create(CreateBookingRequest request, Long userId) {
        BigDecimal unitPrice = BigDecimal.valueOf(1500000);
        int passengerCount = request.passengers().size();
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(passengerCount));

        Booking booking = new Booking();
        booking.setCreatedByUserId(userId);
        booking.setTourId(request.tourId());
        booking.setTourPackageId(request.tourPackageId());
        booking.setBookingCode(generateBookingCode());
        booking.setNumberPassengers(passengerCount);
        booking.setPrimaryContactName(request.primaryContactName().trim());
        booking.setPrimaryContactPhone(request.primaryContactPhone().trim());
        booking.setTotalAmount(totalAmount);
        booking.setStatus(BookingStatus.PENDING_PAYMENT);

        Booking savedBooking = bookingRepository.save(booking);

        for (PassengerRequest pReq : request.passengers()) {
            Passenger passenger = new Passenger();
            passenger.setUserId(userId);
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

        List<BookingPassenger> links = bookingPassengerRepository.findAllByBooking_IdOrderByIdAsc(savedBooking.getId());
        return bookingMapper.toResponse(savedBooking, links);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse get(Long id, Long requesterId, boolean privileged) {
        Booking booking = findBookingById(id);
        if (!privileged && !Objects.equals(booking.getCreatedByUserId(), requesterId)) {
            throw new AppException("You cannot access this booking", HttpStatus.FORBIDDEN);
        }
        List<BookingPassenger> links = bookingPassengerRepository.findAllByBooking_IdOrderByIdAsc(booking.getId());
        return bookingMapper.toResponse(booking, links);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getMine(Long userId) {
        return bookingRepository.findAllByCreatedByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(booking -> {
                    List<BookingPassenger> links = bookingPassengerRepository
                            .findAllByBooking_IdOrderByIdAsc(booking.getId());
                    return bookingMapper.toResponse(booking, links);
                })
                .toList();
    }

    @Override
    @Transactional
    public BookingResponse cancel(Long id, Long userId) {
        Booking booking = findBookingById(id);
        if (!Objects.equals(booking.getCreatedByUserId(), userId)) {
            throw new AppException("You cannot cancel this booking", HttpStatus.FORBIDDEN);
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new AppException("Only CONFIRMED bookings can be cancelled", HttpStatus.CONFLICT);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(booking);

        List<BookingPassenger> links = bookingPassengerRepository.findAllByBooking_IdOrderByIdAsc(saved.getId());
        return bookingMapper.toResponse(saved, links);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailableRoomResponse> getAvailableRooms(UUID tourId, UUID tourPackageId) {
        return List.of();
    }

    @Override
    @Transactional
    public int sendDepartureReminders(LocalDate departureDate) {
        return 0;
    }

    private Booking findBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new AppException("Booking not found: " + id, HttpStatus.NOT_FOUND));
    }

    private String generateBookingCode() {
        for (int attempt = 0; attempt < 10; attempt++) {
            String code = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            if (!bookingRepository.existsByBookingCode(code)) {
                return code;
            }
        }
        throw new AppException("Cannot generate a unique booking code", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}