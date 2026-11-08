
package com.project.chat.repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.chat.model.ConfirmedBooking;

@Repository
public interface ConfirmedBookingRepository
                extends JpaRepository<ConfirmedBooking, Long> {

        Optional<ConfirmedBooking> findByBookingIdAndPrimaryHolderUserId(
                        Long bookingId,
                        UUID primaryHolderUserId);

        boolean existsByBookingIdAndPrimaryHolderUserId(
                        Long bookingId,
                        UUID primaryHolderUserId);

        Optional<ConfirmedBooking> findByBookingCode(String bookingCode);

        List<ConfirmedBooking> findByPrimaryHolderUserIdOrderByConfirmedAtDesc(
                        UUID primaryHolderUserId);
}
