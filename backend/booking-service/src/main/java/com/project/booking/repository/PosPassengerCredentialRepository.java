package com.project.booking.repository;

import com.project.booking.model.PosPassengerCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PosPassengerCredentialRepository extends JpaRepository<PosPassengerCredential, Long> {
    Optional<PosPassengerCredential> findByFingerprint(String fingerprint);
    boolean existsByFingerprint(String fingerprint);
}
