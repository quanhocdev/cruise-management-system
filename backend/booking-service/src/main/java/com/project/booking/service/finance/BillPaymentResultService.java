package com.project.booking.service.finance;

import com.project.booking.model.Bill;
import com.project.booking.model.BookingPassenger;
import com.project.booking.model.enums.BillStatus;
import com.project.booking.repository.BillRepository;
import com.project.booking.repository.BookingPassengerRepository;
import com.project.common.event.BillPaidEvent;
import com.project.common.event.BillPaymentSuccessEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Objects;

@Service
public class BillPaymentResultService {

    private static final String BILL_PAID_TOPIC = "bill-paid-topic";

    private final BillRepository billRepository;
    private final BookingPassengerRepository bookingPassengerRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public BillPaymentResultService(
            BillRepository billRepository,
            BookingPassengerRepository bookingPassengerRepository,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.billRepository = billRepository;
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

        // Idempotent: không phát lại event nếu hóa đơn đã PAID.
        if (bill.getStatus() == BillStatus.PAID) {
            System.out.println(
                    ">>> [BILL PAYMENT] Bill ID " + bill.getId()
                            + " đã PAID, bỏ qua event trùng.");
            return;
        }

        bill.setStatus(BillStatus.PAID);
        billRepository.save(bill);

        Long bookingId = bill.getBooking().getId();

        List<String> nfcCardUids = bookingPassengerRepository
                .findAllByBooking_Id(bookingId)
                .stream()
                .map(BookingPassenger::getNfcCardUid)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(uid -> !uid.isEmpty())
                .distinct()
                .toList();

        BillPaidEvent paidEvent = new BillPaidEvent(
                bill.getId(),
                bookingId,
                nfcCardUids);

        // Chỉ publish sau khi transaction cập nhật PAID đã commit.
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        kafkaTemplate.send(
                                BILL_PAID_TOPIC,
                                String.valueOf(bill.getId()),
                                paidEvent);

                        System.out.println(
                                ">>> [BILL PAID EVENT] Đã gửi event cho billId="
                                        + bill.getId()
                                        + ", bookingId=" + bookingId
                                        + ", số thẻ NFC=" + nfcCardUids.size());
                    }
                });

        System.out.println(
                ">>> [BILL PAYMENT] Bill ID " + bill.getId()
                        + " đã được cập nhật trạng thái PAID.");
    }
}