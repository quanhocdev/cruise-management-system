package com.project.payment.repository;

import com.project.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import com.project.payment.model.enums.PaymentReferenceType;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select p from Payment p where p.id = :id")
    Optional<Payment> findForUpdate(@org.springframework.data.repository.query.Param("id") Long id);

    Optional<Payment> findByTransactionCode(String transactionCode);

    // Thêm dòng này vào Repository của bạn
    Optional<Payment> findByReferenceIdAndReferenceType(Long referenceId, PaymentReferenceType referenceType);

    List<Payment> findAllByReferenceIdAndReferenceTypeOrderByCreatedAtDesc(
            Long referenceId,
            PaymentReferenceType referenceType);
}
