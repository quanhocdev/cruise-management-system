package com.project.booking.repository;

import com.project.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import com.project.booking.model.enums.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByCreatedByUserIdOrderByCreatedAtDesc(Long userId);
    boolean existsByBookingCode(String bookingCode);
    Optional<Booking> findByBookingCodeIgnoreCase(String bookingCode);
    List<Booking> findAllByStatusAndVoyageStartDateAndDepartureReminderSentAtIsNull(BookingStatus status, LocalDate date);
}
