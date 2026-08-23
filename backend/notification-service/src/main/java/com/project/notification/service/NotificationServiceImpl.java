package com.project.notification.service;

import com.project.notification.dto.*;
import com.project.notification.exception.NotificationException;
import com.project.notification.model.*;
import com.project.notification.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository repository;
    private final EmailDeliveryService emailDeliveryService;
    public NotificationServiceImpl(NotificationRepository repository, EmailDeliveryService emailDeliveryService) {
        this.repository = repository; this.emailDeliveryService = emailDeliveryService;
    }
    @Override @Transactional
    public NotificationResponse create(CreateNotificationRequest request) {
        Notification n = new Notification(); n.setRecipientUserId(request.recipientUserId());
        n.setRecipientEmail(blankToNull(request.recipientEmail())); n.setType(request.type());
        n.setTitle(request.title().trim()); n.setMessage(request.message().trim());
        n.setReferenceType(blankToNull(request.referenceType())); n.setReferenceId(request.referenceId());
        n.setCreatedAt(Instant.now());
        n.setEmailStatus(n.getRecipientEmail() == null ? EmailStatus.NOT_REQUESTED : EmailStatus.PENDING);
        Notification saved = repository.save(n); emailDeliveryService.deliver(saved); return toResponse(saved);
    }
    @Override @Transactional(readOnly = true)
    public List<NotificationResponse> getMine(Long userId) {
        return repository.findAllByRecipientUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList();
    }
    @Override @Transactional(readOnly = true)
    public UnreadCountResponse unreadCount(Long userId) { return new UnreadCountResponse(repository.countByRecipientUserIdAndReadAtIsNull(userId)); }
    @Override @Transactional
    public NotificationResponse markRead(Long id, Long userId) {
        Notification n = owned(id, userId); if (n.getReadAt() == null) n.setReadAt(Instant.now());
        return toResponse(repository.save(n));
    }
    @Override @Transactional
    public void markAllRead(Long userId) {
        Instant now = Instant.now(); List<Notification> items = repository.findAllByRecipientUserIdAndReadAtIsNull(userId);
        items.forEach(n -> n.setReadAt(now)); repository.saveAll(items);
    }
    private Notification owned(Long id, Long userId) {
        Notification n = repository.findById(id).orElseThrow(() -> new NotificationException(HttpStatus.NOT_FOUND, "Notification not found: " + id));
        if (!Objects.equals(n.getRecipientUserId(), userId)) throw new NotificationException(HttpStatus.FORBIDDEN, "You cannot access this notification");
        return n;
    }
    private NotificationResponse toResponse(Notification n) { return new NotificationResponse(n.getId(), n.getRecipientUserId(),
        n.getRecipientEmail(), n.getType(), n.getTitle(), n.getMessage(), n.getReferenceType(), n.getReferenceId(),
        n.getEmailStatus(), n.getReadAt(), n.getCreatedAt()); }
    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
