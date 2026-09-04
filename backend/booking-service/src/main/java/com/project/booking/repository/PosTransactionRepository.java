package com.project.booking.repository;

import com.project.booking.model.PosTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PosTransactionRepository extends JpaRepository<PosTransaction, Long> {
    Optional<PosTransaction> findByLocalId(String localId);
}
