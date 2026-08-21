package com.project.notification.service;

import com.project.notification.model.*;
import com.project.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailDeliveryService {
    private final JavaMailSender mailSender;
    private final NotificationRepository repository;
    private final boolean enabled;
    private final String from;
    public EmailDeliveryService(JavaMailSender mailSender, NotificationRepository repository,
        @Value("${notification.email.enabled:false}") boolean enabled,
        @Value("${spring.mail.username:}") String from) {
        this.mailSender = mailSender; this.repository = repository; this.enabled = enabled; this.from = from;
    }
    public void deliver(Notification notification) {
        if (notification.getRecipientEmail() == null || notification.getRecipientEmail().isBlank()) return;
        if (!enabled) {
            notification.setEmailStatus(EmailStatus.NOT_REQUESTED);
            repository.save(notification);
            return;
        }
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            if (!from.isBlank()) mail.setFrom(from);
            mail.setTo(notification.getRecipientEmail()); mail.setSubject(notification.getTitle());
            mail.setText(notification.getMessage()); mailSender.send(mail);
            notification.setEmailStatus(EmailStatus.SENT);
        } catch (RuntimeException ex) {
            notification.setEmailStatus(EmailStatus.FAILED);
        }
        repository.save(notification);
    }
}
