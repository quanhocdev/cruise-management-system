
package com.project.feedback.repository;

import com.project.feedback.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {

    Optional<Feedback> findByInvitationId(UUID invitationId);

    boolean existsByInvitationId(UUID invitationId);
}
