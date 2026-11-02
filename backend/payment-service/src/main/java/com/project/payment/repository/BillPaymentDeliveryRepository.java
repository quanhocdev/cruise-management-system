package com.project.payment.repository;

import com.project.payment.model.Payment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BillPaymentDeliveryRepository
        extends JpaRepository<Payment, Long> {

    @Query(value = """
            SELECT p.id
            FROM payment.payments p
            WHERE p.reference_type = 'BILL'
              AND p.status = 'SUCCESS'
              AND NOT EXISTS (
                  SELECT 1
                  FROM payment.bill_payment_deliveries d
                  WHERE d.payment_id = p.id
              )
            ORDER BY p.id
            LIMIT :limit
            FOR UPDATE OF p SKIP LOCKED
            """, nativeQuery = true)
    List<Long> findPendingPaymentIds(@Param("limit") int limit);

    @Modifying
    @Query(value = """
            INSERT INTO payment.bill_payment_deliveries(payment_id)
            VALUES (:paymentId)
            ON CONFLICT DO NOTHING
            """, nativeQuery = true)
    int markAsDelivered(@Param("paymentId") Long paymentId);
}