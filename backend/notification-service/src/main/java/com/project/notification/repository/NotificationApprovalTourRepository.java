package com.project.notification.repository;

import com.project.notification.model.NotificationApprovalTour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationApprovalTourRepository extends JpaRepository<NotificationApprovalTour, UUID> {

    List<NotificationApprovalTour> findAllByAssignmentTypeOrderByCreatedAtDesc(String assignmentType);

    List<NotificationApprovalTour> findAllByTourIdOrderByCreatedAtDesc(UUID tourId);

    long countByAssignmentTypeAndIsReadFalse(String assignmentType);
}