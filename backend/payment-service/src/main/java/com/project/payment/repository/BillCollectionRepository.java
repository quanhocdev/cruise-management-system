package com.project.payment.repository;

import com.project.payment.model.Payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BillCollectionRepository
        extends JpaRepository<Payment, Long> {

    interface CollectionRow {
        Long getBillId();

        BigDecimal getCollected();

        BigDecimal getReturned();

        BigDecimal getRefundPending();

        Long getSuccessfulPayments();

        Boolean getNeedsReview();
    }

    @Query(value = """
            SELECT
                p.reference_id AS "billId",
                SUM(p.amount) AS "collected",
                SUM(COALESCE(r.returned, 0)) AS "returned",
                SUM(COALESCE(r.pending, 0)) AS "refundPending",
                COUNT(*) AS "successfulPayments",
                BOOL_OR(
                    COALESCE(r.returned, 0) > p.amount
                    OR (
                        p.status = 'REFUNDED'
                        AND COALESCE(r.returned, 0) <> p.amount
                    )
                ) AS "needsReview"
            FROM payment.payments p
            LEFT JOIN (
                SELECT
                    payment_id,
                    SUM(amount) FILTER (
                        WHERE status = 'SUCCEEDED'
                    ) AS returned,
                    SUM(amount) FILTER (
                        WHERE status IN (
                            'QUEUED',
                            'SENDING',
                            'PROCESSING',
                            'UNKNOWN'
                        )
                    ) AS pending
                FROM payment.payment_refunds
                GROUP BY payment_id
            ) r ON r.payment_id = p.id
            WHERE p.reference_type = 'BILL'
              AND p.reference_id IN (:billIds)
              AND p.status IN ('SUCCESS', 'REFUNDED')
            GROUP BY p.reference_id
            """, nativeQuery = true)
    List<CollectionRow> findCollections(
            @Param("billIds") List<Long> billIds);
}