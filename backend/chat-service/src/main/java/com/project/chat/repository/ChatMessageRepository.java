
package com.project.chat.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.chat.model.ChatMessage;

@Repository
public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, UUID> {

    Page<ChatMessage> findByConversation_IdOrderBySentAtDesc(
            UUID conversationId,
            Pageable pageable);

    List<ChatMessage> findByConversation_IdOrderBySentAtAsc(
            UUID conversationId);

    long countByConversation_Id(UUID conversationId);
}
