package com.project.notification.repository;

import com.project.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByRecipientUserIdOrderByCreatedAtDesc(Long userId);
    long countByRecipientUserIdAndReadAtIsNull(Long userId);
    List<Notification> findAllByRecipientUserIdAndReadAtIsNull(Long userId);
}
