package com.project.booking.repository;

import com.project.booking.model.ActivityCruiseUsage;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActivityCruiseUsageRepository
                extends JpaRepository<ActivityCruiseUsage, Long> {

        List<ActivityCruiseUsage> findByBookingPassengerId(Long bookingPassengerId);

        List<ActivityCruiseUsage> findAllByBookingPassengerIdOrderByUsedAtDesc(
                        Long bookingPassengerId);

        long countByActivityCruiseTourId(UUID activityCruiseTourId);

        List<ActivityCruiseUsage> findAllByBookingPassenger_Booking_CreatedByUserIdOrderByUsedAtDesc(
                        UUID userId);

        List<ActivityCruiseUsage> findAllByOrderByUsedAtDesc();

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("""
                        SELECT u
                        FROM ActivityCruiseUsage u
                        WHERE u.bookingPassenger.id = :bookingPassengerId
                          AND u.activityCruiseTourId = :activityCruiseTourId
                          AND u.endedAt IS NULL
                        ORDER BY u.usedAt DESC
                        """)
        List<ActivityCruiseUsage> findActiveUsagesForUpdate(
                        @Param("bookingPassengerId") Long bookingPassengerId,
                        @Param("activityCruiseTourId") UUID activityCruiseTourId);
}