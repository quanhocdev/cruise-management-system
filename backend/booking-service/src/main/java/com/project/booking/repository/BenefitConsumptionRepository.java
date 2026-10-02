package com.project.booking.repository;

import com.project.booking.model.BenefitConsumption;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BenefitConsumptionRepository
        extends JpaRepository<BenefitConsumption, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT bc
            FROM BenefitConsumption bc
            WHERE bc.bookingId = :bookingId
              AND bc.packageBenefitId = :packageBenefitId
            """)
    Optional<BenefitConsumption> findForUpdate(
            @Param("bookingId") Long bookingId,
            @Param("packageBenefitId") UUID packageBenefitId);

    Optional<BenefitConsumption> findByBookingIdAndPackageBenefitId(
            Long bookingId,
            UUID packageBenefitId);

    @Modifying
    @Query(value = """
            INSERT INTO benefit_consumptions
                (
                    booking_id,
                    package_benefit_id,
                    used_quantity,
                    created_at,
                    updated_at
                )
            VALUES
                (
                    :bookingId,
                    :packageBenefitId,
                    0,
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP
                )
            ON CONFLICT (booking_id, package_benefit_id)
            DO NOTHING
            """, nativeQuery = true)
    void createIfNotExists(
            @Param("bookingId") Long bookingId,
            @Param("packageBenefitId") UUID packageBenefitId);
}