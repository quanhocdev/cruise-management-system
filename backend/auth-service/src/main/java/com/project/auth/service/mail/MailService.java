package com.project.auth.service.mail;

public interface MailService {

    void sendOtp(String toEmail, String otp);

    void sendStaffInvitation(
            String toEmail,
            String username,
            String activationLink);

}