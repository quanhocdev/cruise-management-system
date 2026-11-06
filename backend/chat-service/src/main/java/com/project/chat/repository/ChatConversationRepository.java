
package com.project.chat.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.chat.model.ChatConversation;
import com.project.chat.model.enums.ChatDepartment;
import com.project.chat.model.enums.ConversationStatus;

@Repository
public interface ChatConversationRepository
        extends JpaRepository<ChatConversation, UUID> {

    Optional<ChatConversation> findByBooking_BookingIdAndDepartment(
            Long bookingId,
            ChatDepartment department);

    List<ChatConversation> findByBooking_BookingId(Long bookingId);

    List<ChatConversation> findByBooking_BookingIdAndStatus(
            Long bookingId,
            ConversationStatus status);

    boolean existsByBooking_BookingIdAndDepartment(
            Long bookingId,
            ChatDepartment department);
}
