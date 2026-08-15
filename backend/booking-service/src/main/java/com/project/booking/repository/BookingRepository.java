package com.project.booking.repository;

import com.project.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByCreatedByUserIdOrderByCreatedAtDesc(Long userId);
    boolean existsByBookingCode(String bookingCode);
    Optional<Booking> findByBookingCodeIgnoreCase(String bookingCode);
}
