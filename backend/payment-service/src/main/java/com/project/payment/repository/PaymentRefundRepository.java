package com.project.payment.repository;

import com.project.payment.model.PaymentRefund;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRefundRepository
                extends JpaRepository<PaymentRefund, UUID> {

        Optional<PaymentRefund> findByClientRequestId(UUID clientRequestId);

        List<PaymentRefund> findByPaymentIdOrderByCreatedAtDesc(
                        Long paymentId,
                        Pageable pageable);

        @Query("""
                        SELECT COALESCE(SUM(r.amount), 0)
                        FROM PaymentRefund r
                        WHERE r.paymentId = :paymentId
                          AND r.status = 'SUCCEEDED'
                        """)
        BigDecimal sumSucceededAmount(
                        @Param("paymentId") Long paymentId);

        @Query("""
                        SELECT COALESCE(SUM(r.amount), 0)
                        FROM PaymentRefund r
                        WHERE r.paymentId = :paymentId
                          AND r.status NOT IN ('SUCCEEDED', 'REJECTED')
                        """)
        BigDecimal sumPendingAmount(
                        @Param("paymentId") Long paymentId);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("""
                        SELECT r
                        FROM PaymentRefund r
                        WHERE r.id = :id
                        """)
        Optional<PaymentRefund> findByIdForUpdate(
                        @Param("id") UUID id);

        @Modifying
        @Query("""
                        UPDATE PaymentRefund r
                        SET r.status = 'SENDING',
                            r.attemptedAt = :now,
                            r.updatedAt = :now
                        WHERE r.id = :id
                          AND r.status = 'QUEUED'
                        """)
        int claimRefund(
                        @Param("id") UUID id,
                        @Param("now") Instant now);

        List<PaymentRefund> findTop10ByStatusOrderByCreatedAtAsc(
                        String status);

        List<PaymentRefund> findTop10ByStatusInAndUpdatedAtBeforeOrderByUpdatedAtAsc(
                        List<String> statuses,
                        Instant updatedAt);

        @Modifying
        @Query("""
                        UPDATE PaymentRefund r
                        SET r.status = 'UNKNOWN',
                            r.providerCode = 'DISPATCH_INTERRUPTED',
                            r.updatedAt = :now
                        WHERE r.status = 'SENDING'
                          AND r.attemptedAt < :cutoff
                        """)
        int markInterruptedRefunds(
                        @Param("cutoff") Instant cutoff,
                        @Param("now") Instant now);
}