
package com.project.notification.util;

import com.project.common.event.BillPaidEvent;
import com.project.common.event.BillPaidEvent.BillItemDto;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public final class CheckoutInvoicePdfGenerator {

    private static final float LEFT = 45;
    private static final float RIGHT = 550;
    private static final float BOTTOM = 55;
    private static final float ROW_HEIGHT = 42;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private CheckoutInvoicePdfGenerator() {
    }

    public static byte[] generate(BillPaidEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("BillPaidEvent không được null");
        }

        try (PDDocument document = new PDDocument();
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            PDType0Font regular;
            PDType0Font bold;

            try (InputStream regularStream = CheckoutInvoicePdfGenerator.class
                    .getClassLoader()
                    .getResourceAsStream("fonts/NotoSans-Regular.ttf");
                    InputStream boldStream = CheckoutInvoicePdfGenerator.class
                            .getClassLoader()
                            .getResourceAsStream("fonts/NotoSans-Bold.ttf")) {

                if (regularStream == null || boldStream == null) {
                    throw new IllegalStateException(
                            "Không tìm thấy font Noto Sans trong resources/fonts");
                }

                regular = PDType0Font.load(document, regularStream);
                bold = PDType0Font.load(document, boldStream);
            }

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            float y = drawHeader(content, event, regular, bold);

            List<BillItemDto> items = event.items() == null ? List.of() : event.items();

            y = drawTableHeader(content, regular, bold, y);

            for (BillItemDto item : items) {
                if (y - ROW_HEIGHT < BOTTOM + 100) {
                    drawFooter(content, regular, page);
                    content.close();

                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    content = new PDPageContentStream(document, page);

                    y = drawText(content, bold, 15, LEFT, 790,
                            "CHI TIẾT HÓA ĐƠN (tiếp theo)");
                    y -= 25;
                    y = drawTableHeader(content, regular, bold, y);
                }

                y = drawItem(content, item, regular, y);
            }

            if (y - 120 < BOTTOM) {
                drawFooter(content, regular, page);
                content.close();

                page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                content = new PDPageContentStream(document, page);
                y = 780;
            }

            y -= 20;
            drawText(content, bold, 12, LEFT, y,
                    "TỔNG THANH TOÁN: "
                            + money(event.totalAmount()) + " VND");

            y -= 25;
            drawText(content, regular, 9, LEFT, y,
                    event.totalAmount().signum()==0 ? "Không phát sinh thanh toán" : "Trạng thái: " + safe(event.billStatus()));

            y -= 22;
            drawText(content, regular, 9, LEFT, y,
                    "Cảm ơn quý khách đã sử dụng dịch vụ của chúng tôi.");

            drawFooter(content, regular, page);
            content.close();

            document.save(output);
            return output.toByteArray();

        } catch (IOException ex) {
            throw new RuntimeException("Không thể tạo PDF hóa đơn checkout", ex);
        }
    }

    private static float drawHeader(
            PDPageContentStream content,
            BillPaidEvent event,
            PDType0Font regular,
            PDType0Font bold) throws IOException {

        float y = 790;

        y = drawText(content, bold, 20, LEFT, y,
                "HÓA ĐƠN THANH TOÁN");
        y -= 32;

        y = drawText(content, regular, 10, LEFT, y,
                "Mã hóa đơn: " + safe(event.billCode()));
        y -= 20;

        y = drawText(content, regular, 10, LEFT, y,
                "Mã booking: " + safe(event.bookingCode()));
        y -= 20;

        y = drawText(content, regular, 10, LEFT, y,
                "Ngày lập: " + (event.billCreatedAt() == null
                        ? ""
                        : DATE_FORMAT.format(event.billCreatedAt())));
        y -= 20;

        y = drawText(content, regular, 10, LEFT, y,
                "Ngày cập nhật: " + (event.billUpdatedAt() == null
                        ? ""
                        : DATE_FORMAT.format(event.billUpdatedAt())));
        y -= 20;

        y = drawText(content, regular, 10, LEFT, y,
                "Khách hàng: " + safe(event.recipientName()));
        y -= 20;

        y = drawText(content, regular, 10, LEFT, y,
                "Email: " + safe(event.recipientEmail()));
        y -= 20;

        y = drawText(content, regular, 10, LEFT, y,
                "Số hành khách: " + (event.numberPassengers() == null
                        ? 0
                        : event.numberPassengers()));
        y -= 20;

        y = drawText(content, regular, 10, LEFT, y,
                "Trạng thái: " + safe(event.billStatus()));
        y -= 30;

        return y;
    }

    private static float drawTableHeader(
            PDPageContentStream content,
            PDType0Font regular,
            PDType0Font bold,
            float y) throws IOException {

        content.setLineWidth(0.7f);
        content.moveTo(LEFT, y + 8);
        content.lineTo(RIGHT, y + 8);
        content.stroke();

        drawText(content, bold, 8, LEFT, y - 8, "Loại");
        drawText(content, bold, 8, 112, y - 8, "Mã sử dụng");
        drawText(content, bold, 8, 205, y - 8, "SL");
        drawText(content, bold, 8, 240, y - 8, "Đơn giá");
        drawText(content, bold, 8, 330, y - 8, "Giảm giá");
        drawText(content, bold, 8, 425, y - 8, "Thành tiền");

        y -= 28;

        content.moveTo(LEFT, y);
        content.lineTo(RIGHT, y);
        content.stroke();

        return y - 12;
    }

    private static float drawItem(
            PDPageContentStream content,
            BillItemDto item,
            PDType0Font font,
            float y) throws IOException {

        drawText(content, font, 8, LEFT, y,
                truncate(safe(item.usageType()), 12));
        drawText(content, font, 8, 112, y,
                safe(item.usageId() == null ? "" : item.usageId().toString()));
        drawText(content, font, 8, 205, y,
                String.valueOf(item.quantity() == null ? 0 : item.quantity()));
        drawText(content, font, 8, 240, y, money(item.unitPrice()));
        drawText(content, font, 8, 330, y, money(item.discountAmount()));
        drawText(content, font, 8, 425, y, money(item.finalAmount()));

        y -= ROW_HEIGHT;

        content.moveTo(LEFT, y + 15);
        content.lineTo(RIGHT, y + 15);
        content.setLineWidth(0.3f);
        content.stroke();

        return y;
    }

    private static void drawFooter(
            PDPageContentStream content,
            PDType0Font font,
            PDPage page) throws IOException {

        drawText(content, font, 8, LEFT, 30,
                "Cruise System - Biên nhận dịch vụ");
    }

    private static float drawText(
            PDPageContentStream content,
            PDType0Font font,
            int size,
            float x,
            float y,
            String text) throws IOException {

        content.beginText();
        content.setFont(font, size);
        content.newLineAtOffset(x, y);
        content.showText(safe(text));
        content.endText();
        return y;
    }

    private static String money(BigDecimal value) {
        if (value == null) {
            return "0";
        }

        DecimalFormat formatter = new DecimalFormat(
                "#,##0.##",
                DecimalFormatSymbols.getInstance(Locale.US));

        return formatter.format(value);
    }

    private static String truncate(String value, int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 1) + "…";
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
