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

    // 2. Hàm chuyên gửi email HTML đính kèm mã QR cho Booking (Đã có sẵn logic
    // chuẩn)
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

            String htmlContent = "<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>"
                    + "<h2 style='color: #0d6efd;'>Xác Nhận Đặt Tour Thành Công!</h2>"
                    + "<p>Xin chào <b>" + notification.getRecipientName() + "</b>,</p>"
                    + "<p>" + notification.getMessage() + "</p>"
                    + "<div style='background: #f9f9f9; padding: 15px; border-radius: 5px; margin: 15px 0;'>"
                    + "<p><b>Mã đặt tour:</b> <span style='color: #007bff; font-weight: bold;'>" + bookingCode
                    + "</span></p>"
                    + "</div>"
                    + "<p>Vui lòng xuất trình mã QR đính kèm khi check-in.</p>"
                    // Thêm text-align: center để căn giữa ảnh QR
                    + "<div style='text-align: center; margin: 25px 0;'><img src='cid:qrCode' style='width: 200px; height: 200px; display: inline-block;'/></div>"
                    + "<p style='margin-top: 30px; font-size: 14px; color: #666;'>"
                    + "Bạn có thể xem chi tiết hoặc quản lý các đơn đặt tour của mình tại phần <b>My Booking</b> của hệ thống."
                    + "</p>"
                    + "<p>Trân trọng,<br><b>Cruise System Team</b></p>"
                    + "</div>";

            helper.setText(htmlContent, true);

            byte[] qrBytes = QRCodeGenerator.generateQRCodeImage(bookingCode, 200, 200);
            helper.addInline("qrCode", new ByteArrayResource(qrBytes), "image/png");

            mailSender.send(mimeMessage);
            notification.setEmailStatus(EmailStatus.SENT);
        } catch (MessagingException | RuntimeException ex) {
            notification.setEmailStatus(EmailStatus.FAILED);
            ex.printStackTrace();
        }
        repository.save(notification);
    }

    // 3. Hàm nhận Event từ Kafka, đóng gói vào Notification và gọi hàm
    // deliverBookingQrEmail
    public void sendBookingConfirmationEmail(com.project.common.event.BookingConfirmedEvent event) {
        Notification notification = new Notification();
        notification.setRecipientUserId(event.recipientUserId());
        notification.setRecipientEmail(event.recipientEmail());
        notification.setRecipientName(event.recipientName());
        notification.setTitle("Xác nhận đặt tour thành công - " + event.bookingCode());
        notification.setMessage(String.format(
                "Đơn đặt tour của bạn đã được xác nhận thanh toán thành công với tổng số tiền %.2f VND cho %d hành khách.",
                event.totalAmount(), event.numberPassengers()));

        // Tận dụng hoàn toàn logic gửi QR và lưu log DB có sẵn
        deliverBookingQrEmail(notification, event.bookingCode());
    }
}