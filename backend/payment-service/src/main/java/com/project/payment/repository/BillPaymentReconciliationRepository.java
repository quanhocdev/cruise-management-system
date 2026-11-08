package com.project.payment.repository;

import com.project.payment.model.Payment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BillPaymentReconciliationRepository
        extends JpaRepository<Payment, Long> {

    @Query(value = """
            SELECT p.id
            FROM payment.payments p
            LEFT JOIN payment.bill_query_schedule q
                ON q.payment_id = p.id
            WHERE p.reference_type = 'BILL'
              AND p.status IN ('PENDING', 'EXPIRED')
              AND p.expires_at < CURRENT_TIMESTAMP
              AND (
                  q.next_attempt_at IS NULL
                  OR q.next_attempt_at <= CURRENT_TIMESTAMP
              )
            ORDER BY p.id
            LIMIT 10
            """, nativeQuery = true)
    List<Long> findDuePaymentIds();

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO payment.bill_query_schedule(
                payment_id,
                next_attempt_at
            )
            VALUES (
                :paymentId,
                CURRENT_TIMESTAMP + INTERVAL '5 minutes'
            )
            ON CONFLICT (payment_id)
            DO UPDATE SET
                next_attempt_at = EXCLUDED.next_attempt_at
            WHERE payment.bill_query_schedule.next_attempt_at
                <= CURRENT_TIMESTAMP
            """, nativeQuery = true)
    int claimPayment(@Param("paymentId") Long paymentId);

    @Modifying
    @Query(value = """
            INSERT INTO payment.bill_payment_locks(bill_id)
            VALUES (:billId)
            ON CONFLICT DO NOTHING
            """, nativeQuery = true)
    int createBillLock(@Param("billId") Long billId);

    @Query(value = """
            SELECT bill_id
            FROM payment.bill_payment_locks
            WHERE bill_id = :billId
            FOR UPDATE
            """, nativeQuery = true)
    Long lockBill(@Param("billId") Long billId);
}