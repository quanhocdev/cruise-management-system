package com.project.auth.service.mail;

public interface MailService {

    void sendOtp(String toEmail, String otp);

}