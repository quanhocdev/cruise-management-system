package com.project.notification.service;

import com.project.notification.model.*;
import com.project.notification.repository.NotificationRepository;
import com.project.notification.util.QRCodeGenerator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
        this.mailSender = mailSender;
        this.repository = repository;
        this.enabled = enabled;
        this.from = from;
    }

    // 1. Giữ nguyên hàm cũ cho thông báo text thông thường
    public void deliver(Notification notification) {
        if (notification.getRecipientEmail() == null || notification.getRecipientEmail().isBlank())
            return;
        if (!enabled) {
            notification.setEmailStatus(EmailStatus.NOT_REQUESTED);
            repository.save(notification);
            return;
        }
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            if (!from.isBlank())
                mail.setFrom(from);
            mail.setTo(notification.getRecipientEmail());
            mail.setSubject(notification.getTitle());
            mail.setText(notification.getMessage());
            mailSender.send(mail);
            notification.setEmailStatus(EmailStatus.SENT);
        } catch (RuntimeException ex) {
            notification.setEmailStatus(EmailStatus.FAILED);
        }
        repository.save(notification);
    }

    // 2. THÊM HÀM MỚI: Chuyên gửi email HTML đính kèm mã QR cho Booking
    public void deliverBookingQrEmail(Notification notification, String bookingCode) {
        if (notification.getRecipientEmail() == null || notification.getRecipientEmail().isBlank())
            return;
        if (!enabled) {
            notification.setEmailStatus(EmailStatus.NOT_REQUESTED);
            repository.save(notification);
            return;
        }
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            if (!from.isBlank())
                helper.setFrom(from);
            helper.setTo(notification.getRecipientEmail());
            helper.setSubject(notification.getTitle());

            // Nội dung HTML hiển thị thông tin và chèn ảnh QR thông qua Content-ID
            // (cid:qrCode)
            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px;'>"
                    + "<h2 style='color: #0d6efd;'>Xác Nhận Đặt Tour Thành Công</h2>"
                    + "<p>" + notification.getMessage() + "</p>"
                    + "<p>Mã đặt chỗ của bạn: <strong style='font-size: 16px; color: #d63384;'>" + bookingCode
                    + "</strong></p>"
                    + "<p>Vui lòng xuất trình mã QR dưới đây khi check-in tại quầy:</p>"
                    + "<div style='margin: 20px 0;'><img src='cid:qrCode' style='width: 200px; height: 200px;'/></div>"
                    + "<p>Trân trọng,<br><b>Cruise System Team</b></p>"
                    + "</div>";

            helper.setText(htmlContent, true);

            byte[] qrBytes = QRCodeGenerator.generateQRCodeImage(bookingCode, 200, 200);
            helper.addInline("qrCode", new ByteArrayResource(qrBytes), "image/png");

            mailSender.send(mimeMessage);
            notification.setEmailStatus(EmailStatus.SENT);
        } catch (MessagingException | RuntimeException ex) {
            notification.setEmailStatus(EmailStatus.FAILED);
        }
        repository.save(notification);
    }
}