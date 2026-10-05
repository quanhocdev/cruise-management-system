package com.project.booking.repository;

import com.project.booking.model.ActivityVisitUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityVisitUsageRepository
        extends JpaRepository<ActivityVisitUsage, Long> {

    long countByVisitTourId(UUID visitTourId);

    List<ActivityVisitUsage> findAllByBookingPassengerIdOrderByUsedAtDesc(
            Long bookingPassengerId);

    List<ActivityVisitUsage> findAllByBookingPassenger_Booking_CreatedByUserIdOrderByUsedAtDesc(
            Long userId);
}