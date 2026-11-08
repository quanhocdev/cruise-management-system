
package com.project.chat.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.chat.model.ChatConversation;
import com.project.chat.model.ConversationParticipant;
import com.project.chat.model.enums.ConversationStatus;
import com.project.chat.model.enums.ParticipantRole;

@Repository
public interface ConversationParticipantRepository
                extends JpaRepository<ConversationParticipant, UUID> {

        List<ConversationParticipant> findByConversation_Id(UUID conversationId);

        Optional<ConversationParticipant> findByConversation_IdAndUserId(
                        UUID conversationId,
                        UUID userId);

        boolean existsByConversation_IdAndUserId(
                        UUID conversationId,
                        UUID userId);

        List<ConversationParticipant> findByUserId(UUID userId);

        void deleteByConversation_IdAndUserId(
                        UUID conversationId,
                        UUID userId);

        @Query("""
                        SELECT COUNT(p)
                        FROM ConversationParticipant p
                        JOIN p.conversation c
                        WHERE p.userId = :managerId
                          AND p.participantRole = :managerRole
                          AND c.status = :status
                        """)
        long countConversationsByManager(
                        @Param("managerId") UUID managerId,
                        @Param("managerRole") ParticipantRole managerRole,
                        @Param("status") ConversationStatus status);

        @Query("""
                        SELECT p.conversation
                        FROM ConversationParticipant p
                        JOIN p.conversation c
                        WHERE p.userId = :managerId
                          AND p.participantRole = :managerRole
                          AND c.status = :status
                        """)
        List<ChatConversation> findConversationsByManager(
                        @Param("managerId") UUID managerId,
                        @Param("managerRole") ParticipantRole managerRole,
                        @Param("status") ConversationStatus status);

}
