
package com.project.booking.repository;

import com.project.booking.model.PosCommand;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PosCommandRepository extends JpaRepository<PosCommand, UUID> {

    Optional<PosCommand> findByClientRequestId(UUID clientRequestId);

    Optional<PosCommand> findByClientRequestIdAndActorId(
            UUID clientRequestId,
            UUID actorId);

    @Modifying
    @Query(value = """
            INSERT INTO pos_commands (
                id,
                client_request_id,
                actor_id,
                operation,
                payload_hash,
                status,
                created_at,
                updated_at
            )
            VALUES (
                :id,
                :clientRequestId,
                :actorId,
                :operation,
                :payloadHash,
                'PROCESSING',
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            ON CONFLICT (client_request_id) DO NOTHING
            """, nativeQuery = true)
    int insertIfAbsent(
            @Param("id") UUID id,
            @Param("clientRequestId") UUID clientRequestId,
            @Param("actorId") UUID actorId,
            @Param("operation") String operation,
            @Param("payloadHash") String payloadHash);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                SELECT pc
                FROM PosCommand pc
                WHERE pc.clientRequestId = :clientRequestId
            """)
    Optional<PosCommand> findByClientRequestIdForUpdate(
            @Param("clientRequestId") UUID clientRequestId);
}
