
package com.project.notification.service;

import com.project.common.event.FeedbackInvitationCreatedEvent;
import com.project.notification.dto.response.NotificationRealtimePayload;
import com.project.notification.dto.response.NotificationResponse;
import com.project.notification.mapper.NotificationMapper;
import com.project.notification.model.Notification;
import com.project.notification.model.NotificationType;
import com.project.notification.repository.NotificationRepository;

import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private static final String FEEDBACK_REFERENCE_TYPE = "FEEDBACK_INVITATION";

    private static final String USER_NOTIFICATION_DESTINATION = "/queue/notifications";

    private static final String EVENT_NEW_NOTIFICATION = "NEW_NOTIFICATION";

    private static final String EVENT_UNREAD_COUNT_UPDATED = "UNREAD_COUNT_UPDATED";

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(
            NotificationRepository notificationRepository,
            SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    // 1. CREATE FEEDBACK INVITATION NOTIFICATION
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

        Notification savedNotification = notificationRepository.save(notification);

        NotificationResponse response = NotificationMapper.toResponse(savedNotification);

        // Chỉ gửi socket sau khi DB commit thành công.
        sendAfterCommit(
                recipientUserId,
                EVENT_NEW_NOTIFICATION,
                response);
    }

    // 2. GET ALL NOTIFICATIONS
    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications(
            UUID recipientUserId) {

        return notificationRepository
                .findByRecipientUserIdOrderByCreatedAtDesc(recipientUserId)
                .stream()
                .map(NotificationMapper::toResponse)
                .toList();
    }

    // 3. GET UNREAD NOTIFICATION COUNT
    @Transactional(readOnly = true)
    public long countUnreadNotifications(UUID recipientUserId) {

        return notificationRepository
                .countByRecipientUserIdAndIsReadFalse(recipientUserId);
    }

    // 4. MARK ONE NOTIFICATION AS READ
    @Transactional
    public NotificationResponse markAsRead(
            UUID notificationId,
            UUID recipientUserId) {

        Notification notification = notificationRepository
                .findById(notificationId)
                .filter(item -> item.getRecipientUserId()
                        .equals(recipientUserId))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Notification not found"));

        if (!notification.isRead()) {

            notification.setRead(true);

            notification = notificationRepository.save(notification);

            sendAfterCommit(
                    recipientUserId,
                    EVENT_UNREAD_COUNT_UPDATED,
                    null);
        }

        return NotificationMapper.toResponse(notification);
    }

    // 5. MARK ALL NOTIFICATIONS AS READ
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

        sendAfterCommit(
                recipientUserId,
                EVENT_UNREAD_COUNT_UPDATED,
                null);

        return unreadNotifications.size();
    }

    // 6. SEND WEBSOCKET AFTER TRANSACTION COMMIT
    private void sendAfterCommit(
            UUID recipientUserId,
            String eventType,
            NotificationResponse notificationResponse) {

        if (!TransactionSynchronizationManager
                .isSynchronizationActive()) {
            throw new IllegalStateException(
                    "Transaction synchronization is not active");
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {

                        long unreadCount = notificationRepository
                                .countByRecipientUserIdAndIsReadFalse(
                                        recipientUserId);

                        NotificationRealtimePayload payload = new NotificationRealtimePayload(
                                eventType,
                                notificationResponse,
                                unreadCount);

                        messagingTemplate.convertAndSendToUser(
                                recipientUserId.toString(),
                                USER_NOTIFICATION_DESTINATION,
                                payload);
                    }
                });
    }
}
