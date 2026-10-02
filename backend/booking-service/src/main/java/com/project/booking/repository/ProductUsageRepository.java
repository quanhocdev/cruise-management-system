package com.project.booking.repository;

import com.project.booking.model.ProductUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductUsageRepository
                extends JpaRepository<ProductUsage, Long> {

        List<ProductUsage> findAllByBookingPassenger_IdOrderByUsedAtDesc(
                        Long bookingPassengerId);

        List<ProductUsage> findAllByBookingPassenger_Booking_CreatedByUserIdOrderByUsedAtDesc(
                        Long userId);

        @Query("""
                        SELECT COALESCE(SUM(pu.quantity), 0)
                        FROM ProductUsage pu
                        WHERE pu.bookingPassenger.booking.id = :bookingId
                          AND pu.productTourId = :productTourId
                        """)
        long sumUsedQuantityByBookingIdAndProductTourId(
                        @Param("bookingId") Long bookingId,
                        @Param("productTourId") UUID productTourId);
}