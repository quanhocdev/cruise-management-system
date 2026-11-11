
package com.project.notification.repository;

import com.project.notification.model.Notification;
import com.project.notification.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository
        extends JpaRepository<Notification, UUID> {

    // Lấy danh sách thông báo của một người dùng
    List<Notification> findByRecipientUserIdOrderByCreatedAtDesc(
            UUID recipientUserId);

    // Lấy danh sách thông báo chưa đọc
    List<Notification> findByRecipientUserIdAndIsReadFalseOrderByCreatedAtDesc(
            UUID recipientUserId);

    // Đếm số thông báo chưa đọc
    long countByRecipientUserIdAndIsReadFalse(
            UUID recipientUserId);

    // Kiểm tra thông báo đã tồn tại để tránh Kafka xử lý trùng
    boolean existsByRecipientUserIdAndReferenceTypeAndReferenceId(
            UUID recipientUserId,
            String referenceType,
            UUID referenceId);

    // Lọc thông báo theo loại
    List<Notification> findByRecipientUserIdAndTypeOrderByCreatedAtDesc(
            UUID recipientUserId,
            NotificationType type);
}
