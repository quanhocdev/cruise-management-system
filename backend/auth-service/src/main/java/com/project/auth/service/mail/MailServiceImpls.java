package com.project.auth.service.mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpls implements MailService {

    private final JavaMailSender mailSender;

    public MailServiceImpls(JavaMailSender mailSender) {
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
                        + "Mã OTP có hiệu lực trong 5 phút.");

        mailSender.send(message);
    }

    @Override
    public void sendStaffInvitation(
            String toEmail,
            String username,
            String activationLink) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);

        message.setSubject(
                "Cruise System - Kích hoạt tài khoản nhân viên");

        message.setText(
                "Xin chào " + username + ",\n\n"
                        + "Tài khoản nhân viên của bạn đã được tạo "
                        + "trên hệ thống Cruise System.\n\n"
                        + "Tài khoản: " + username + "\n\n"
                        + "Vui lòng bấm vào liên kết bên dưới để "
                        + "kích hoạt tài khoản và thiết lập mật khẩu:\n\n"
                        + activationLink + "\n\n"
                        + "Liên kết có hiệu lực trong 10 phút.\n\n"
                        + "Nếu bạn không thực hiện yêu cầu này, "
                        + "vui lòng bỏ qua email.\n\n"
                        + "Cruise System");

        mailSender.send(message);
    }
}