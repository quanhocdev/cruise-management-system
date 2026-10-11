
package com.project.notification.service;

import com.project.common.event.BookingConfirmedEvent;
import com.project.common.event.BookingCreatedEvent;
import com.project.notification.model.*;
import com.project.notification.repository.NotificationBookingRepository;
import com.project.notification.util.BookingConfirmationPdfGenerator;
import com.project.notification.util.QRCodeGenerator;
import java.math.BigDecimal;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.project.common.event.BillPaidEvent;
import com.project.notification.util.CheckoutInvoicePdfGenerator;

@Service
public class NotificationBookingService {

    private final JavaMailSender mailSender;
    private final NotificationBookingRepository repository;
    private final boolean enabled;
    private final String from;

    public NotificationBookingService(
            JavaMailSender mailSender,
            NotificationBookingRepository repository,
            @Value("${notification.email.enabled:false}") boolean enabled,
            @Value("${spring.mail.username:}") String from) {

        this.mailSender = mailSender;
        this.repository = repository;
        this.enabled = enabled;
        this.from = from;
    }

    public void deliver(NotificationBooking notification) {
        if (notification.getRecipientEmail() == null
                || notification.getRecipientEmail().isBlank()) {
            return;
        }

        if (!enabled) {
            notification.setEmailStatus(EmailStatus.NOT_REQUESTED);
            repository.save(notification);
            return;
        }

        try {
            SimpleMailMessage mail = new SimpleMailMessage();

            if (!from.isBlank()) {
                mail.setFrom(from);
            }

            mail.setTo(notification.getRecipientEmail());
            mail.setSubject(notification.getTitle());
            mail.setText(notification.getMessage());

            mailSender.send(mail);
            notification.setEmailStatus(EmailStatus.SENT);

        } catch (RuntimeException ex) {
            notification.setEmailStatus(EmailStatus.FAILED);
            ex.printStackTrace();
        }

        repository.save(notification);
    }

    /**
     * Gửi email xác nhận đặt tour, nhúng QR và đính kèm PDF.
     */
    public void deliverBookingQrEmail(
            NotificationBooking notification,
            String bookingCode,
            BigDecimal totalAmount,
            Integer numberPassengers) {

        if (notification.getRecipientEmail() == null
                || notification.getRecipientEmail().isBlank()) {
            return;
        }

        if (!enabled) {
            notification.setEmailStatus(EmailStatus.NOT_REQUESTED);
            repository.save(notification);
            return;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            if (!from.isBlank()) {
                helper.setFrom(from);
            }

            helper.setTo(notification.getRecipientEmail());
            helper.setSubject(notification.getTitle());

            String htmlContent = "<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>"
                    + "<h2 style='color: #0d6efd;'>Xác Nhận Đặt Tour Thành Công!</h2>"
                    + "<p>Xin chào <b>" + safe(notification.getRecipientName()) + "</b>,</p>"
                    + "<p>" + safe(notification.getMessage()) + "</p>"
                    + "<div style='background: #f9f9f9; padding: 15px; border-radius: 5px; margin: 15px 0;'>"
                    + "<p><b>Mã đặt tour:</b> <span style='color: #007bff; font-weight: bold;'>"
                    + safe(bookingCode) + "</span></p>"
                    + "<p><b>Số hành khách:</b> " + numberPassengers + "</p>"
                    + "<p><b>Tổng tiền:</b> "
                    + (totalAmount == null
                            ? "0 VND"
                            : String.format(
                                    java.util.Locale.US,
                                    "%,.0f VND",
                                    totalAmount))
                    + "</p>"
                    + "</div>"
                    + "<p>Vui lòng xuất trình mã QR khi check-in.</p>"
                    + "<div style='text-align: center; margin: 25px 0;'>"
                    + "<img src='cid:qrCode' style='width: 200px; height: 200px;'/>"
                    + "</div>"
                    + "<p>Bạn có thể xem chi tiết hoặc quản lý các đơn đặt tour tại phần "
                    + "<b>My Booking</b> của hệ thống.</p>"
                    + "<p>Trân trọng,<br><b>Cruise System Team</b></p>"
                    + "</div>";

            helper.setText(htmlContent, true);

            // 1. Tạo QR dùng chung cho email và PDF.
            byte[] qrBytes = QRCodeGenerator.generateQRCodeImage(bookingCode, 200, 200);

            // 2. Nhúng QR vào nội dung email.
            helper.addInline(
                    "qrCode",
                    new ByteArrayResource(qrBytes),
                    "image/png");

            // 3. Tạo PDF xác nhận đặt tour.
            byte[] pdfBytes = BookingConfirmationPdfGenerator.generate(
                    bookingCode,
                    notification.getRecipientName(),
                    totalAmount,
                    numberPassengers,
                    qrBytes);

            // 4. Đính kèm PDF vào email.
            helper.addAttachment(
                    "Xac-nhan-dat-tour-" + safeFilename(bookingCode) + ".pdf",
                    new ByteArrayResource(pdfBytes),
                    "application/pdf");

            // 5. Gửi email.
            mailSender.send(mimeMessage);

            notification.setEmailStatus(EmailStatus.SENT);

        } catch (MessagingException | RuntimeException ex) {
            notification.setEmailStatus(EmailStatus.FAILED);
            ex.printStackTrace();
        }

        repository.save(notification);
    }

    /**
     * Nhận event xác nhận thanh toán từ Kafka.
     */
    public void sendBookingConfirmationEmail(BookingConfirmedEvent event) {

        NotificationBooking notification = new NotificationBooking();

        notification.setRecipientUserId(event.recipientUserId());
        notification.setRecipientEmail(event.recipientEmail());
        notification.setRecipientName(event.recipientName());

        notification.setTitle(
                "Xác nhận đặt tour thành công - " + event.bookingCode());

        notification.setMessage(String.format(
                java.util.Locale.US,
                "Đơn đặt tour của bạn đã được xác nhận thanh toán thành công "
                        + "với tổng số tiền %,.0f VND cho %d hành khách.",
                event.totalAmount(),
                event.numberPassengers()));

        notification.setType(NotificationType.BOOKING_CONFIRMED);

        deliverBookingQrEmail(
                notification,
                event.bookingCode(),
                event.totalAmount(),
                event.numberPassengers());
    }

    /**
     * Lưu thông báo chờ thanh toán, không gửi email QR.
     */
    public void savePendingPaymentNotification(BookingCreatedEvent event) {

        NotificationBooking notification = new NotificationBooking();

        notification.setRecipientUserId(event.userId());

        notification.setTitle("Đơn đặt tour mới đang chờ thanh toán");

        notification.setMessage(String.format(
                java.util.Locale.US,
                "Bạn vừa tạo đơn đặt tour thành công. "
                        + "Vui lòng hoàn tất thanh toán trước thời hạn để giữ chỗ. "
                        + "Tổng tiền: %,.0f VND",
                event.totalPrice()));

        notification.setType(NotificationType.PENDING_PAYMENT);
        notification.setEmailStatus(EmailStatus.NOT_REQUESTED);

        repository.save(notification);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String safeFilename(String value) {
        if (value == null || value.isBlank()) {
            return "booking";
        }

        return value.replaceAll("[^a-zA-Z0-9_-]", "_");
    }

    /**
     * Lưu thông báo checkout và gửi email kèm PDF hóa đơn chi tiết.
     * Không ảnh hưởng luồng email xác nhận booking hiện tại.
     */
    public void sendCheckoutInvoiceEmail(BillPaidEvent event) {
        if (event == null || event.billId() == null) {
            throw new IllegalArgumentException(
                    "BillPaidEvent hoặc billId không được null");
        }

        NotificationBooking notification = new NotificationBooking();
        notification.setRecipientUserId(event.recipientUserId());
        notification.setRecipientEmail(event.recipientEmail());
        notification.setRecipientName(event.recipientName());

        notification.setType(NotificationType.BILL_PAID);
        notification.setReferenceType("BILL");
        notification.setReferenceId(event.billId());

        notification.setTitle(
                "Hóa đơn checkout đã thanh toán - " + safe(event.billCode()));

        notification.setMessage(
                "Xin chào " + safe(event.recipientName())
                        + ", hóa đơn " + safe(event.billCode())
                        + " cho booking " + safe(event.bookingCode())
                        + " đã được thanh toán thành công. "
                        + "Tổng thanh toán: "
                        + (event.totalAmount() == null
                                ? "0"
                                : String.format(
                                        java.util.Locale.US,
                                        "%,.0f",
                                        event.totalAmount()))
                        + " VND. Hóa đơn chi tiết được đính kèm trong email.");

        // Nếu chức năng gửi email đang tắt, vẫn lưu thông báo.
        if (!enabled) {
            notification.setEmailStatus(EmailStatus.NOT_REQUESTED);
            repository.save(notification);
            return;
        }

        if (event.recipientEmail() == null
                || event.recipientEmail().isBlank()) {
            notification.setEmailStatus(EmailStatus.FAILED);
            repository.save(notification);
            System.err.println(
                    ">>> [CHECKOUT EMAIL] Thiếu email người nhận, billId="
                            + event.billId());
            return;
        }

        try {
            byte[] pdfBytes = CheckoutInvoicePdfGenerator.generate(event);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            if (from != null && !from.isBlank()) {
                helper.setFrom(from);
            }

            helper.setTo(event.recipientEmail());
            helper.setSubject(notification.getTitle());

            String html = "<div style='font-family:Arial,sans-serif;"
                    + "line-height:1.7;color:#333'>"
                    + "<h2>Thanh toán checkout thành công!</h2>"
                    + "<p>Xin chào <b>" + safe(event.recipientName()) + "</b>,</p>"
                    + "<p>Cảm ơn bạn đã sử dụng dịch vụ của Cruise System.</p>"
                    + "<p>Hóa đơn checkout của bạn đã được thanh toán thành công.</p>"
                    + "<div style='padding:16px;background:#f5f7fa;border-radius:8px'>"
                    + "<p><b>Mã hóa đơn:</b> "
                    + safe(event.billCode()) + "</p>"
                    + "<p><b>Mã booking:</b> "
                    + safe(event.bookingCode()) + "</p>"
                    + "<p><b>Số hành khách:</b> "
                    + (event.numberPassengers() == null
                            ? 0
                            : event.numberPassengers())
                    + "</p>"
                    + "<p><b>Tổng thanh toán:</b> "
                    + (event.totalAmount() == null
                            ? "0"
                            : String.format(
                                    java.util.Locale.US,
                                    "%,.0f",
                                    event.totalAmount()))
                    + " VND</p>"
                    + "<p><b>Trạng thái:</b> "
                    + safe(event.billStatus()) + "</p>"
                    + "</div>"
                    + "<p>Vui lòng xem file PDF đính kèm để kiểm tra toàn bộ "
                    + "chi tiết dịch vụ, số lượng, đơn giá, giảm giá và thành tiền.</p>"
                    + "<p>Trân trọng,<br><b>Cruise System Team</b></p>"
                    + "</div>";

            helper.setText(html, true);

            helper.addAttachment(
                    "Hoa-don-checkout-" + safeFilename(event.billCode()) + ".pdf",
                    new ByteArrayResource(pdfBytes),
                    "application/pdf");

            mailSender.send(mimeMessage);

            notification.setEmailStatus(EmailStatus.SENT);

            System.out.println(
                    ">>> [CHECKOUT EMAIL] Đã gửi hóa đơn: billId="
                            + event.billId()
                            + ", email=" + event.recipientEmail());

        } catch (MessagingException | RuntimeException ex) {
            notification.setEmailStatus(EmailStatus.FAILED);
            System.err.println(
                    ">>> [CHECKOUT EMAIL] Gửi thất bại, billId="
                            + event.billId()
                            + ": " + ex.getMessage());
            ex.printStackTrace();
        }

        repository.save(notification);
    }

}
