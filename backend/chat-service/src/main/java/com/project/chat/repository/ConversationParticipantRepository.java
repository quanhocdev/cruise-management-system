
package com.project.chat.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.chat.model.ConversationParticipant;

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
}
