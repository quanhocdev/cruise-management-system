package com.project.tour.repository;

import com.project.tour.model.NfcCard;
import com.project.tour.model.enums.NfcCardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NfcCardRepository extends JpaRepository<NfcCard, UUID> {
    Optional<NfcCard> findByCardUid(String cardUid);

    boolean existsByCardUid(String cardUid);

    // Lấy danh sách thẻ NFC có sẵn ( AVAILABLE )
    List<NfcCard> findAllByStatus(NfcCardStatus status);
}