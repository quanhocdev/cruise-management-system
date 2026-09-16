package com.project.notification.service;

import com.project.notification.model.*;
import com.project.notification.repository.NotificationBookingRepository;
import com.project.notification.util.QRCodeGenerator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.project.common.event.BookingConfirmedEvent;

@Service
public class NotificationBookingService {
    private final JavaMailSender mailSender;
    private final NotificationBookingRepository repository;
    private final boolean enabled;
    private final String from;

    public NotificationBookingService(JavaMailSender mailSender, NotificationBookingRepository repository,
            @Value("${notification.email.enabled:false}") boolean enabled,
            @Value("${spring.mail.username:}") String from) {
        this.mailSender = mailSender;
        this.repository = repository;
        this.enabled = enabled;
        this.from = from;
    }

    public void deliver(NotificationBooking notification) {
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

    // Hàm gửi email đính kèm mã QR cho Booking
    public void deliverBookingQrEmail(NotificationBooking notification, String bookingCode) {
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

    // Hàm nhận Event xác nhận thanh toán từ Kafka (Gửi mail QR + Lưu log)
    public void sendBookingConfirmationEmail(BookingConfirmedEvent event) {
        NotificationBooking notification = new NotificationBooking();
        notification.setRecipientUserId(event.recipientUserId());
        notification.setRecipientEmail(event.recipientEmail());
        notification.setRecipientName(event.recipientName());
        notification.setTitle("Xác nhận đặt tour thành công - " + event.bookingCode());
        notification.setMessage(String.format(
                "Đơn đặt tour của bạn đã được xác nhận thanh toán thành công với tổng số tiền %.2f VND cho %d hành khách.",
                event.totalAmount(), event.numberPassengers()));

        notification.setType(NotificationType.BOOKING_CONFIRMED);
        deliverBookingQrEmail(notification, event.bookingCode());
    }

    // Lưu thông báo chờ thanh toán (PENDING_PAYMENT) khi vừa tạo đơn (Hiện chuông,
    // không gửi mail QR)
    public void savePendingPaymentNotification(com.project.common.event.BookingCreatedEvent event) {
        NotificationBooking notification = new NotificationBooking();
        notification.setRecipientUserId(event.userId());
        notification.setTitle("Đơn đặt tour mới đang chờ thanh toán");
        notification.setMessage(String.format(
                "Bạn vừa tạo đơn đặt tour thành công. Vui lòng hoàn tất thanh toán trước thời hạn để giữ chỗ. Tổng tiền: %.2f VND",
                event.totalPrice()));

        notification.setType(NotificationType.PENDING_PAYMENT);
        notification.setEmailStatus(EmailStatus.NOT_REQUESTED);

        repository.save(notification);
    }
}