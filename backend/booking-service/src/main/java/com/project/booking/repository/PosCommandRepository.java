
package com.project.booking.repository;

import com.project.booking.model.PosCommand;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PosCommandRepository extends JpaRepository<PosCommand, UUID> {

    Optional<PosCommand> findByClientRequestId(UUID clientRequestId);

    Optional<PosCommand> findByClientRequestIdAndActorId(
            UUID clientRequestId,
            UUID actorId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                SELECT pc
                FROM PosCommand pc
                WHERE pc.clientRequestId = :clientRequestId
            """)
    Optional<PosCommand> findByClientRequestIdForUpdate(
            @Param("clientRequestId") UUID clientRequestId);
}
