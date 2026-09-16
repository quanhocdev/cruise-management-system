package com.project.notification.listener;

import com.project.common.event.SendOtpEvent;
import com.project.common.event.SendStaffInvitationEvent;
import com.project.notification.service.NotificationAuthService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AuthEventListener {

    private final NotificationAuthService authEmailService;

    public AuthEventListener(NotificationAuthService authEmailService) {
        this.authEmailService = authEmailService;
    }

    @KafkaListener(topics = "send-otp-topic", groupId = "notification-service-group")
    public void handleSendOtp(SendOtpEvent event) {
        System.out.println(">>> [NOTIFICATION] Nhận yêu cầu gửi OTP cho: " + event.recipientEmail());
        authEmailService.sendOtpEmail(event);
    }

    @KafkaListener(topics = "staff-invitation-topic", groupId = "notification-service-group")
    public void handleStaffInvitation(SendStaffInvitationEvent event) {
        System.out.println(">>> [NOTIFICATION] Nhận yêu cầu mời nhân viên cho: " + event.recipientEmail());
        authEmailService.sendStaffInvitationEmail(event);
    }
}