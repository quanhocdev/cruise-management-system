package com.project.payment.repository;

import com.project.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import com.project.payment.model.enums.PaymentReferenceType;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionCode(String transactionCode);

    List<Payment> findAllByReferenceIdAndReferenceTypeOrderByCreatedAtDesc(
            Long referenceId,
            PaymentReferenceType referenceType);
}
