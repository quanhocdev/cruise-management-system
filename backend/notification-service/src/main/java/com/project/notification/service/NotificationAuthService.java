package com.project.notification.service;

import com.project.common.event.SendOtpEvent;
import com.project.common.event.SendStaffInvitationEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class NotificationAuthService {

    private final JavaMailSender mailSender;
    private final boolean enabled;
    private final String from;

    public NotificationAuthService(
            JavaMailSender mailSender,
            @Value("${notification.email.enabled:false}") boolean enabled,
            @Value("${spring.mail.username:}") String from) {
        this.mailSender = mailSender;
        this.enabled = enabled;
        this.from = from;
    }

    // 1. Gửi Email OTP đăng ký tài khoản
    public void sendOtpEmail(SendOtpEvent event) {
        if (event.recipientEmail() == null || event.recipientEmail().isBlank())
            return;
        if (!enabled) {
            System.out
                    .println(">>> [AUTH EMAIL] Email đang bị tắt (notification.email.enabled=false). Bỏ qua gửi OTP.");
            return;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            if (!from.isBlank())
                helper.setFrom(from);
            helper.setTo(event.recipientEmail());
            helper.setSubject("Cruise System - Xác thực tài khoản của bạn");

            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px; background-color: #ffffff;'>"
                    + "<h2 style='color: #0d6efd; text-align: center; margin-bottom: 20px;'>Xác Thực Tài Khoản</h2>"
                    + "<p style='font-size: 16px; color: #333333;'>Xin chào,</p>"
                    + "<p style='font-size: 16px; color: #333333;'>Cảm ơn bạn đã đăng ký tài khoản tại <b>Cruise System</b>. Vui lòng sử dụng mã OTP bên dưới để hoàn tất quá trình xác thực email:</p>"
                    + "<div style='text-align: center; margin: 30px 0;'>"
                    + "<span style='display: inline-block; font-size: 32px; font-weight: bold; letter-spacing: 6px; color: #0d6efd; background: #f8f9fa; padding: 15px 30px; border-radius: 6px; border: 1px dashed #0d6efd;'>"
                    + event.otp()
                    + "</span>"
                    + "</div>"
                    + "<p style='font-size: 14px; color: #666666; text-align: center;'>Mã OTP này có hiệu lực trong vòng <b>5 phút</b>. Không chia sẻ mã này cho bất kỳ ai.</p>"
                    + "<hr style='border: none; border-top: 1px solid #e0e0e0; margin: 25px 0;'>"
                    + "<p style='font-size: 12px; color: #999999; text-align: center;'>Trân trọng,<br><b>Cruise System Team</b></p>"
                    + "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            System.out.println(">>> [AUTH EMAIL] Đã gửi HTML OTP thành công tới: " + event.recipientEmail());

        } catch (MessagingException | RuntimeException e) {
            e.printStackTrace();
            System.err.println(">>> [AUTH EMAIL] Gửi HTML OTP thất bại: " + e.getMessage());
        }
    }

    // 2. Gửi Email Mời Nhân Viên / Kích hoạt tài khoản (Giao diện HTML đẹp)
    public void sendStaffInvitationEmail(SendStaffInvitationEvent event) {
        if (event.recipientEmail() == null || event.recipientEmail().isBlank())
            return;
        if (!enabled) {
            System.out.println(">>> [AUTH EMAIL] Email đang bị tắt. Bỏ qua gửi thư mời nhân viên.");
            return;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            if (!from.isBlank())
                helper.setFrom(from);
            helper.setTo(event.recipientEmail());
            helper.setSubject("Cruise System - Kích hoạt tài khoản nhân viên");

            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px; background-color: #ffffff;'>"
                    + "<h2 style='color: #198754; text-align: center; margin-bottom: 20px;'>Thư Mời Nhận Việc & Kích Hoạt Tài Khoản</h2>"
                    + "<p style='font-size: 16px; color: #333333;'>Xin chào <b>" + event.username() + "</b>,</p>"
                    + "<p style='font-size: 16px; color: #333333;'>Tài khoản nhân viên của bạn đã được khởi tạo trên hệ thống quản lý <b>Cruise System</b>.</p>"
                    + "<div style='background: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #198754;'>"
                    + "<p style='margin: 0; font-size: 15px; color: #333;'><b>Tên đăng nhập:</b> " + event.username()
                    + "</p>"
                    + "</div>"
                    + "<p style='font-size: 16px; color: #333333;'>Vui lòng bấm vào nút bên dưới để thiết lập mật khẩu và kích hoạt tài khoản của bạn:</p>"
                    + "<div style='text-align: center; margin: 30px 0;'>"
                    + "<a href='" + event.activationLink()
                    + "' style='background-color: #198754; color: white; padding: 12px 25px; text-decoration: none; font-size: 16px; font-weight: bold; border-radius: 5px; display: inline-block;'>Kích Hoạt Tài Khoản Ngay</a>"
                    + "</div>"
                    + "<p style='font-size: 13px; color: #666666; word-break: break-all;'>Nếu nút trên không hoạt động, hãy copy đường dẫn này dán vào trình duyệt:<br><a href='"
                    + event.activationLink() + "' style='color: #0d6efd;'>" + event.activationLink() + "</a></p>"
                    + "<p style='font-size: 14px; color: #dc3545; margin-top: 20px;'><i>Lưu ý: Liên kết kích hoạt này có hiệu lực trong vòng 10 phút.</i></p>"
                    + "<hr style='border: none; border-top: 1px solid #e0e0e0; margin: 25px 0;'>"
                    + "<p style='font-size: 12px; color: #999999; text-align: center;'>Trân trọng,<br><b>Cruise System Admin Team</b></p>"
                    + "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            System.out.println(
                    ">>> [AUTH EMAIL] Đã gửi HTML thư mời nhân viên thành công tới: " + event.recipientEmail());

        } catch (MessagingException | RuntimeException e) {
            e.printStackTrace();
            System.err.println(">>> [AUTH EMAIL] Gửi HTML thư mời nhân viên thất bại: " + e.getMessage());
        }
    }
}