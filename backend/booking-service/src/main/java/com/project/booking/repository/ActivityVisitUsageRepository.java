package com.project.booking.repository;

import com.project.booking.model.ActivityVisitUsage;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
                        UUID userId);

        List<ActivityVisitUsage> findAllByOrderByUsedAtDesc();

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("""
                        SELECT u
                        FROM ActivityVisitUsage u
                        WHERE u.bookingPassenger.id = :bookingPassengerId
                          AND u.visitTourId = :visitTourId
                          AND u.endedAt IS NULL
                        ORDER BY u.usedAt DESC
                        """)
        List<ActivityVisitUsage> findActiveUsagesForUpdate(
                        @Param("bookingPassengerId") Long bookingPassengerId,
                        @Param("visitTourId") UUID visitTourId);
}