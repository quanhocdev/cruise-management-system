package com.project.booking.service;

import com.project.booking.dto.*;
import com.project.booking.exception.BookingException;
import com.project.booking.model.Booking;
import com.project.booking.model.enums.BookingStatus;
import com.project.booking.repository.BookingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    public BookingServiceImpl(BookingRepository repository) { this.repository = repository; }

    @Override @Transactional
    public BookingResponse create(CreateBookingRequest request, Long userId) {
        Instant now = Instant.now();
        Booking booking = new Booking();
        booking.setUserId(userId); booking.setScheduleId(request.scheduleId()); booking.setRoomId(request.roomId());
        booking.setGuestCount(request.guestCount()); booking.setTotalAmount(request.totalAmount());
        booking.setStatus(BookingStatus.PENDING_PAYMENT); booking.setCreatedAt(now); booking.setUpdatedAt(now);
        return toResponse(repository.save(booking));
    }

    @Override @Transactional(readOnly = true)
    public BookingResponse get(Long id, Long requesterId, boolean privileged) {
        Booking booking = find(id);
        if (!privileged && !booking.getUserId().equals(requesterId))
            throw new BookingException(HttpStatus.FORBIDDEN, "You cannot access this booking");
        return toResponse(booking);
    }

    @Override @Transactional(readOnly = true)
    public List<BookingResponse> getMine(Long userId) {
        return repository.findAllByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    @Override @Transactional
    public BookingResponse cancel(Long id, Long userId) {
        Booking booking = find(id);
        if (!booking.getUserId().equals(userId))
            throw new BookingException(HttpStatus.FORBIDDEN, "You cannot cancel this booking");
        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT)
            throw new BookingException(HttpStatus.CONFLICT, "Only a pending booking can be cancelled");
        booking.setStatus(BookingStatus.CANCELLED); booking.setUpdatedAt(Instant.now());
        return toResponse(repository.save(booking));
    }

    @Override @Transactional(readOnly = true)
    public BookingPaymentContext getPaymentContext(Long id) {
        Booking booking = find(id);
        return new BookingPaymentContext(booking.getId(), booking.getUserId(), booking.getTotalAmount(), booking.getStatus());
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
        booking.setStatus(BookingStatus.CONFIRMED); booking.setPaymentId(paymentId); booking.setUpdatedAt(Instant.now());
        return toResponse(repository.save(booking));
    }

    private Booking find(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new BookingException(HttpStatus.NOT_FOUND, "Booking not found: " + id));
    }
    private BookingResponse toResponse(Booking b) {
        return new BookingResponse(b.getId(), b.getUserId(), b.getScheduleId(), b.getRoomId(), b.getGuestCount(),
            b.getTotalAmount(), b.getStatus(), b.getPaymentId(), b.getCreatedAt(), b.getUpdatedAt());
    }
}
