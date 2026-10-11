
package com.project.common.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BillPaidEvent(
        // Thông tin hóa đơn
        Long billId,
        String billCode,
        BigDecimal totalAmount,
        String billStatus,
        LocalDateTime billCreatedAt,
        LocalDateTime billUpdatedAt,

        // Thông tin booking
        Long bookingId,
        String bookingCode,
        Integer numberPassengers,
        BigDecimal bookingTotalAmount,

        // Thông tin người nhận email
        Long recipientUserId,
        String recipientEmail,
        String recipientName,

        // Danh sách thẻ NFC cần trả về kho
        List<String> nfcCardUids,

        // Chi tiết các dòng hóa đơn
        List<BillItemDto> items) {
    public record BillItemDto(
            Long billItemId,
            Long bookingPassengerId,
            String usageType,
            Long usageId,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal discountAmount,
            BigDecimal finalAmount) {
    }
}
