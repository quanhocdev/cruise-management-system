
package com.project.notification.service;

import com.project.common.event.FeedbackInvitationCreatedEvent;
import com.project.notification.dto.response.NotificationResponse;
import com.project.notification.mapper.NotificationMapper;
import com.project.notification.model.Notification;
import com.project.notification.model.NotificationType;
import com.project.notification.repository.NotificationRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private static final String FEEDBACK_REFERENCE_TYPE = "FEEDBACK_INVITATION";

    private final NotificationRepository notificationRepository;

    public NotificationService(
            NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // =====================================================
    // 1. CREATE FEEDBACK INVITATION NOTIFICATION
    // =====================================================

    @Transactional
    public void createFeedbackInvitationNotification(
            FeedbackInvitationCreatedEvent event) {
        if (event == null) {
            throw new IllegalArgumentException(
                    "Feedback invitation event must not be null");
        }

        UUID recipientUserId = event.reviewerUserId();
        UUID invitationId = event.invitationId();

        if (recipientUserId == null || invitationId == null) {
            throw new IllegalArgumentException(
                    "Reviewer user ID and invitation ID must not be null");
        }

        boolean alreadyExists = notificationRepository
                .existsByRecipientUserIdAndReferenceTypeAndReferenceId(
                        recipientUserId,
                        FEEDBACK_REFERENCE_TYPE,
                        invitationId);

        if (alreadyExists) {
            return;
        }

        String targetName = event.targetName();

        if (targetName == null || targetName.isBlank()) {
            targetName = "trải nghiệm của bạn";
        }

        Notification notification = new Notification();

        notification.setRecipientUserId(recipientUserId);
        notification.setType(NotificationType.FEEDBACK_INVITATION);
        notification.setTitle("Bạn có lời mời đánh giá mới");
        notification.setMessage(
                "Hãy chia sẻ cảm nhận của bạn về "
                        + targetName
                        + ". Ý kiến của bạn giúp chúng tôi cải thiện chất lượng dịch vụ.");

        notification.setReferenceType(FEEDBACK_REFERENCE_TYPE);
        notification.setReferenceId(invitationId);
        notification.setRead(false);

        try {
            notificationRepository.save(notification);
        } catch (DataIntegrityViolationException exception) {

            // Kiểm tra lại nếu event đã được xử lý đồng thời.
            boolean duplicate = notificationRepository
                    .existsByRecipientUserIdAndReferenceTypeAndReferenceId(
                            recipientUserId,
                            FEEDBACK_REFERENCE_TYPE,
                            invitationId);

            if (!duplicate) {
                throw exception;
            }
        }
    }

    // =====================================================
    // 2. GET ALL NOTIFICATIONS
    // =====================================================

    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications(
            UUID recipientUserId) {
        return notificationRepository
                .findByRecipientUserIdOrderByCreatedAtDesc(recipientUserId)
                .stream()
                .map(NotificationMapper::toResponse)
                .toList();
    }

    // =====================================================
    // 3. GET UNREAD NOTIFICATION COUNT
    // =====================================================

    @Transactional(readOnly = true)
    public long countUnreadNotifications(UUID recipientUserId) {
        return notificationRepository
                .countByRecipientUserIdAndIsReadFalse(recipientUserId);
    }

    // =====================================================
    // 4. MARK ONE NOTIFICATION AS READ
    // =====================================================

    @Transactional
    public NotificationResponse markAsRead(
            UUID notificationId,
            UUID recipientUserId) {
        Notification notification = notificationRepository
                .findById(notificationId)
                .filter(item -> item.getRecipientUserId().equals(recipientUserId))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Notification not found"));

        if (!notification.isRead()) {
            notification.setRead(true);
            notification = notificationRepository.save(notification);
        }

        return NotificationMapper.toResponse(notification);
    }

    // =====================================================
    // 5. MARK ALL NOTIFICATIONS AS READ
    // =====================================================

    @Transactional
    public int markAllAsRead(UUID recipientUserId) {

        List<Notification> unreadNotifications = notificationRepository
                .findByRecipientUserIdAndIsReadFalseOrderByCreatedAtDesc(
                        recipientUserId);

        if (unreadNotifications.isEmpty()) {
            return 0;
        }

        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
        }

        notificationRepository.saveAll(unreadNotifications);

        return unreadNotifications.size();
    }
}
