
package com.project.tour.repository;

import com.project.tour.model.admin.NfcCard;
import com.project.tour.model.enums.NfcCardStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NfcCardRepository extends JpaRepository<NfcCard, UUID> {

    Optional<NfcCard> findByCardUid(String cardUid);

    boolean existsByCardUid(String cardUid);

    List<NfcCard> findAllByStatus(NfcCardStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT card FROM NfcCard card WHERE card.cardUid = :cardUid")
    Optional<NfcCard> findByCardUidForUpdate(
            @Param("cardUid") String cardUid);
}
