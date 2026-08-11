package com.project.payment.repository;

import com.project.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionCode(String transactionCode);

    Optional<Payment> findByReferenceIdAndReferenceType(
            Long referenceId,
            com.project.payment.model.enums.PaymentReferenceType referenceType);
}
