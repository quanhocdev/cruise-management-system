package com.project.booking.repository;

import com.project.booking.model.ServiceUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceUsageRepository extends JpaRepository<ServiceUsage, Long> {

    /**
     * Tìm usage đang active của một passenger
     * đối với một service tour.
     *
     * Active nghĩa là:
     * - Chưa checkout: endedAt IS NULL
     * - Nếu service có thời hạn thì chưa hết hạn.
     */
    @Query("""
            SELECT su
            FROM ServiceUsage su
            WHERE su.bookingPassenger.id = :bookingPassengerId
              AND su.serviceTourId = :serviceTourId
              AND su.endedAt IS NULL
              AND (
                    su.expiresAt IS NULL
                    OR su.expiresAt > :now
                  )
            ORDER BY su.usedAt DESC
            """)
    Optional<ServiceUsage> findActiveUsage(
            @Param("bookingPassengerId") Long bookingPassengerId,
            @Param("serviceTourId") UUID serviceTourId,
            @Param("now") LocalDateTime now);

    /**
     * Đếm số passenger đang sử dụng service.
     *
     * Mỗi ServiceUsage tương ứng với 1 passenger,
     * nên chỉ cần COUNT(*), không cần SUM(quantity).
     */
    @Query("""
            SELECT COUNT(su)
            FROM ServiceUsage su
            WHERE su.serviceTourId = :serviceTourId
              AND su.endedAt IS NULL
              AND (
                    su.expiresAt IS NULL
                    OR su.expiresAt > :now
                  )
            """)
    long countActiveUsages(
            @Param("serviceTourId") UUID serviceTourId,
            @Param("now") LocalDateTime now);

    /**
     * Lấy lịch sử sử dụng service của một passenger.
     */
    List<ServiceUsage> findAllByBookingPassenger_IdOrderByUsedAtDesc(
            Long bookingPassengerId);

    /**
     * Lấy lịch sử sử dụng một service tour.
     */
    List<ServiceUsage> findAllByServiceTourIdOrderByUsedAtDesc(
            UUID serviceTourId);

    /**
     * Lấy toàn bộ lịch sử sử dụng service
     * của tất cả passenger thuộc các booking do user tạo.
     *
     * Quan hệ:
     *
     * ServiceUsage
     * -> BookingPassenger
     * -> Booking
     * -> createdByUserId
     */
    List<ServiceUsage> findAllByBookingPassenger_Booking_CreatedByUserIdOrderByUsedAtDesc(
            Long userId);
}