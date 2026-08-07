package com.project.auth.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;


    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    @Override
    public void sendOtp(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("Cruise System - Xác thực email");

        message.setText(
                "Xin chào,\n\n"
                + "Mã OTP xác thực email của bạn là: "
                + otp
                + "\n\n"
                + "Mã OTP có hiệu lực trong 5 phút."
        );

        mailSender.send(message);
    }
}