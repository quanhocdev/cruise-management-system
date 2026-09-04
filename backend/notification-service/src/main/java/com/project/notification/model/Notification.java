package com.project.notification.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notifications_user_created", columnList = "recipient_user_id,created_at"),
    @Index(name = "idx_notifications_user_read", columnList = "recipient_user_id,read_at")
})
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "recipient_user_id", nullable = false) private Long recipientUserId;
    @Column(name = "recipient_email", length = 255) private String recipientEmail;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 40) private NotificationType type;
    @Column(nullable = false, length = 200) private String title;
    @Column(nullable = false, columnDefinition = "TEXT") private String message;
    @Column(name = "reference_type", length = 40) private String referenceType;
    @Column(name = "reference_id") private Long referenceId;
    @Enumerated(EnumType.STRING) @Column(name = "email_status", nullable = false, length = 20) private EmailStatus emailStatus;
    @Column(name = "read_at") private Instant readAt;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    public Long getId() { return id; } public void setId(Long v) { id = v; }
    public Long getRecipientUserId() { return recipientUserId; } public void setRecipientUserId(Long v) { recipientUserId = v; }
    public String getRecipientEmail() { return recipientEmail; } public void setRecipientEmail(String v) { recipientEmail = v; }
    public NotificationType getType() { return type; } public void setType(NotificationType v) { type = v; }
    public String getTitle() { return title; } public void setTitle(String v) { title = v; }
    public String getMessage() { return message; } public void setMessage(String v) { message = v; }
    public String getReferenceType() { return referenceType; } public void setReferenceType(String v) { referenceType = v; }
    public Long getReferenceId() { return referenceId; } public void setReferenceId(Long v) { referenceId = v; }
    public EmailStatus getEmailStatus() { return emailStatus; } public void setEmailStatus(EmailStatus v) { emailStatus = v; }
    public Instant getReadAt() { return readAt; } public void setReadAt(Instant v) { readAt = v; }
    public Instant getCreatedAt() { return createdAt; } public void setCreatedAt(Instant v) { createdAt = v; }
}
