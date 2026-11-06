
package com.project.chat.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.chat.model.ChatConversation;
import com.project.chat.model.enums.ChatDepartment;
import com.project.chat.model.enums.ConversationStatus;
import com.project.chat.model.enums.ParticipantRole;

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

        @Query("""
                        SELECT c
                        FROM ChatConversation c
                        WHERE c.status = :status
                          AND c.department = :department
                          AND NOT EXISTS (
                              SELECT p.id
                              FROM ConversationParticipant p
                              WHERE p.conversation = c
                                AND p.participantRole = :managerRole
                          )
                        ORDER BY c.createdAt ASC
                        """)
        List<ChatConversation> findUnassignedConversations(
                        @Param("status") ConversationStatus status,
                        @Param("department") ChatDepartment department,
                        @Param("managerRole") ParticipantRole managerRole);

}
