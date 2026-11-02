
package com.project.payment.repository;

import com.project.payment.model.Payment;
import com.project.payment.model.enums.PaymentReferenceType;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.id = :id")
    Optional<Payment> findForUpdate(@Param("id") Long id);

    Optional<Payment> findByTransactionCode(String transactionCode);

    Optional<Payment> findByReferenceIdAndReferenceType(
            Long referenceId,
            PaymentReferenceType referenceType);

    List<Payment> findAllByReferenceIdAndReferenceTypeOrderByCreatedAtDesc(
            Long referenceId,
            PaymentReferenceType referenceType);
}
