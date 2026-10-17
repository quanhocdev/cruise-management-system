
package com.project.booking.service.finance.bill;

import com.project.booking.model.Bill;
import com.project.booking.model.BillItem;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.model.enums.BillStatus;
import com.project.booking.repository.BillItemRepository;
import com.project.booking.repository.BillRepository;
import com.project.booking.repository.BookingPassengerRepository;
import com.project.common.event.BillPaidEvent;
import com.project.common.event.BillPaidEvent.BillItemDto;
import com.project.common.event.BillPaymentSuccessEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.booking.service.account.OnboardAccountService;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Objects;

@Service
public class BillPaymentResultService {

        private static final String BILL_PAID_TOPIC = "bill-paid-topic";

        private final BillRepository billRepository;
        private final BillItemRepository billItemRepository;
        private final BookingPassengerRepository bookingPassengerRepository;
        private final KafkaTemplate<String, Object> kafkaTemplate;
        private final OnboardAccountService accounts;

        public BillPaymentResultService(
                        BillRepository billRepository,
                        BillItemRepository billItemRepository,
                        BookingPassengerRepository bookingPassengerRepository,
                        KafkaTemplate<String, Object> kafkaTemplate, OnboardAccountService accounts) {
                this.accounts = accounts;
                this.billRepository = billRepository;
                this.billItemRepository = billItemRepository;
                this.bookingPassengerRepository = bookingPassengerRepository;
                this.kafkaTemplate = kafkaTemplate;
        }

        @Transactional
        public void processPaymentSuccess(BillPaymentSuccessEvent event) {
                if (event == null || event.billId() == null) {
                        throw new IllegalArgumentException(
                                        "Bill payment success event or bill ID must not be null");
                }

                Bill bill = billRepository.findById(event.billId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Bill not found with ID: " + event.billId()));

                // Tránh xử lý lại hóa đơn đã thanh toán.
                if (bill.getStatus() == BillStatus.PAID) {
                        System.out.println(
                                        ">>> [BILL PAYMENT] Bill ID " + bill.getId()
                                                        + " đã PAID, bỏ qua event trùng.");
                        return;
                }

                Booking booking = bill.getBooking();

                if (booking == null || booking.getId() == null) {
                        throw new IllegalStateException(
                                        "Bill không có booking hợp lệ: " + bill.getId());
                }

                Long bookingId = booking.getId();

                // Lấy toàn bộ chi tiết hóa đơn.
                List<BillItemDto> items = billItemRepository
                                .findAllByBill_IdOrderByIdAsc(bill.getId())
                                .stream()
                                .map(this::toBillItemDto)
                                .toList();

                // Lấy các thẻ NFC đang được gán cho hành khách của booking.
                List<String> nfcCardUids = bookingPassengerRepository
                                .findAllByBooking_Id(bookingId)
                                .stream()
                                .map(BookingPassenger::getNfcCardUid)
                                .filter(Objects::nonNull)
                                .map(String::trim)
                                .filter(uid -> !uid.isEmpty())
                                .distinct()
                                .toList();

                // Đánh dấu hóa đơn đã thanh toán.
                bill.setStatus(BillStatus.PAID);
                billRepository.save(bill);
                accounts.markClosed(bookingId);

                BillPaidEvent paidEvent = new BillPaidEvent(
                                bill.getId(),
                                bill.getBillCode(),
                                bill.getTotalAmount(),
                                BillStatus.PAID.name(),
                                bill.getCreatedAt(),
                                bill.getUpdatedAt(),

                                bookingId,
                                booking.getBookingCode(),
                                booking.getNumberPassengers(),
                                booking.getTotalAmount(),

                                booking.getCreatedByUserId(),
                                booking.getPrimaryContactEmail(),
                                booking.getPrimaryContactName(),

                                nfcCardUids,
                                items);

                // Publish event sau khi transaction commit thành công.
                TransactionSynchronizationManager.registerSynchronization(
                                new TransactionSynchronization() {
                                        @Override
                                        public void afterCommit() {
                                                kafkaTemplate.send(
                                                                BILL_PAID_TOPIC,
                                                                String.valueOf(bill.getId()),
                                                                paidEvent).whenComplete((result, exception) -> {
                                                                        if (exception != null) {
                                                                                System.err.println(
                                                                                                ">>> [BILL PAID EVENT] Gửi thất bại: billId="
                                                                                                                + bill.getId()
                                                                                                                + ", error="
                                                                                                                + exception.getMessage());
                                                                                return;
                                                                        }

                                                                        System.out.println(
                                                                                        ">>> [BILL PAID EVENT] Đã gửi event: billId="
                                                                                                        + bill.getId()
                                                                                                        + ", bookingId="
                                                                                                        + bookingId
                                                                                                        + ", số thẻ NFC="
                                                                                                        + nfcCardUids.size()
                                                                                                        + ", số dòng hóa đơn="
                                                                                                        + items.size());
                                                                });
                                        }
                                });

                System.out.println(
                                ">>> [BILL PAYMENT] Bill ID " + bill.getId()
                                                + " đã được cập nhật trạng thái PAID.");
        }

        private BillItemDto toBillItemDto(BillItem item) {
                return new BillItemDto(
                                item.getId(),
                                item.getBookingPassengerId(),
                                item.getUsageType(),
                                item.getUsageId(),
                                item.getQuantity(),
                                item.getUnitPrice(),
                                item.getDiscountAmount(),
                                item.getFinalAmount());
        }
}
