package com.project.auth.service;

public interface MailService {

    void sendOtp(String toEmail, String otp);

}