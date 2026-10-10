package com.project.notification.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class BookingConfirmationPdfGenerator {

    private BookingConfirmationPdfGenerator() {
    }

    public static byte[] generate(
            String bookingCode,
            String recipientName,
            BigDecimal totalAmount,
            Integer numberPassengers,
            byte[] qrBytes) {

        if (totalAmount == null) {
            throw new IllegalArgumentException(
                    "Total amount must not be null");
        }

        try (
                PDDocument document = new PDDocument();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Tạo trang PDF A4
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            // Đọc font Noto Sans từ resources/fonts
            InputStream regularStream = BookingConfirmationPdfGenerator.class
                    .getClassLoader()
                    .getResourceAsStream("fonts/NotoSans-Regular.ttf");

            InputStream boldStream = BookingConfirmationPdfGenerator.class
                    .getClassLoader()
                    .getResourceAsStream("fonts/NotoSans-Bold.ttf");

            if (regularStream == null || boldStream == null) {
                if (regularStream != null) {
                    regularStream.close();
                }
                if (boldStream != null) {
                    boldStream.close();
                }

                throw new IllegalStateException(
                        "Không tìm thấy font Noto Sans. "
                                + "Hãy kiểm tra thư mục resources/fonts.");
            }

            try (
                    regularStream;
                    boldStream) {
                PDType0Font normalFont = PDType0Font.load(document, regularStream);

                PDType0Font titleFont = PDType0Font.load(document, boldStream);

                try (PDPageContentStream content = new PDPageContentStream(document, page)) {

                    // Tiêu đề
                    drawText(
                            content, titleFont, 20, 55, 780,
                            "XÁC NHẬN ĐẶT TOUR THÀNH CÔNG");

                    drawText(
                            content, normalFont, 11, 55, 750,
                            "Đơn đặt tour của bạn đã được xác nhận thành công.");

                    // Thông tin đặt tour
                    drawText(
                            content, titleFont, 13, 55, 695,
                            "THÔNG TIN ĐẶT TOUR");

                    drawText(
                            content, normalFont, 11, 55, 665,
                            "Mã đặt tour: " + safe(bookingCode));

                    drawText(
                            content, normalFont, 11, 55, 640,
                            "Người đặt: " + safe(recipientName));

                    drawText(
                            content, normalFont, 11, 55, 615,
                            "Số hành khách: "
                                    + (numberPassengers == null
                                            ? 0
                                            : numberPassengers));

                    drawText(
                            content, normalFont, 11, 55, 590,
                            "Tổng tiền: " + formatAmount(totalAmount) + " VND");

                    drawText(
                            content, normalFont, 11, 55, 565,
                            "Trạng thái thanh toán: Đã thanh toán");

                    // Mã QR
                    if (qrBytes != null && qrBytes.length > 0) {
                        PDImageXObject qrImage = PDImageXObject.createFromByteArray(
                                document,
                                qrBytes,
                                "booking-qr");

                        drawText(
                                content, titleFont, 13, 55, 510,
                                "MÃ QR ĐẶT TOUR");

                        content.drawImage(
                                qrImage, 55, 315, 180, 180);
                    }

                    drawText(
                            content, normalFont, 10, 55, 270,
                            "Vui lòng xuất trình mã QR khi làm thủ tục check-in.");

                    drawText(
                            content, normalFont, 10, 55, 90,
                            "Trân trọng, Cruise System Team");
                }
            }

            // Xuất PDF thành byte[]
            document.save(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException(
                    "Không thể tạo PDF xác nhận đặt tour.", e);
        }
    }

    private static void drawText(
            PDPageContentStream content,
            PDType0Font font,
            int fontSize,
            float x,
            float y,
            String text) throws IOException {

        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
    }

    private static String formatAmount(BigDecimal amount) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.US);

        DecimalFormat formatter = new DecimalFormat("#,##0.##", symbols);

        formatter.setGroupingUsed(true);

        return formatter.format(amount);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
