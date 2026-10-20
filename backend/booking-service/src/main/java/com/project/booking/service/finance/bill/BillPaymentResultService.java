
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

import org.springframework.stereotype.Service;
import com.project.booking.service.account.OnboardAccountService;

import java.util.List;
import java.util.Objects;

@Service
public class BillPaymentResultService {

        private final BillRepository billRepository;
        private final BillItemRepository billItemRepository;
        private final BookingPassengerRepository bookingPassengerRepository;
        private final OnboardAccountService accounts;
        private final com.project.booking.client.PaymentClient payments;
        private final org.springframework.transaction.support.TransactionTemplate transaction;
        private final org.springframework.jdbc.core.JdbcTemplate jdbc;
        private final com.project.booking.repository.BookingRepository bookings;

        public BillPaymentResultService(
                        BillRepository billRepository,
                        BillItemRepository billItemRepository,
                        BookingPassengerRepository bookingPassengerRepository,
                        OnboardAccountService accounts,
                        com.project.booking.client.PaymentClient payments,
                        org.springframework.transaction.PlatformTransactionManager manager,
                        org.springframework.jdbc.core.JdbcTemplate jdbc,
                        com.project.booking.repository.BookingRepository bookings) {
                this.bookings=bookings;
                this.payments=payments;
                this.transaction=new org.springframework.transaction.support.TransactionTemplate(manager);
                this.jdbc=jdbc;
                this.accounts = accounts;
                this.billRepository = billRepository;
                this.billItemRepository = billItemRepository;
                this.bookingPassengerRepository = bookingPassengerRepository;
        }

        public void processPaymentSuccess(BillPaymentSuccessEvent event) {
                if(event==null || event.billId()==null || event.paymentId()==null || !"SUCCESS".equals(event.status()))
                        throw new IllegalArgumentException("Invalid bill payment result");
                // Verify at the payment service before holding database locks.
                var verified=payments.getBillPayments(event.billId()).stream()
                        .filter(p -> event.paymentId().equals(p.paymentId()) && "SUCCESS".equals(p.status()))
                        .findFirst().orElseThrow(() -> new IllegalArgumentException("Payment not confirmed"));
                transaction.executeWithoutResult(s -> {
                    var bookingId=jdbc.queryForObject("SELECT booking_id FROM booking.bills WHERE id=?",Long.class,event.billId());
                    accounts.lockAccount(bookings.findById(bookingId).orElseThrow());
                    var bill=billRepository.findById(event.billId()).orElseThrow();
                    if(!verified.billId().equals(bill.getId()) || !verified.payerId().equals(bill.getBooking().getCreatedByUserId())
                            || !verified.payerId().equals(event.userId()) || verified.amount().compareTo(bill.getTotalAmount())!=0)
                        throw new IllegalArgumentException("Bill payment correlation mismatch");
                    complete(event);
                });
        }

        private void complete(BillPaymentSuccessEvent event) {
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

                try {
                    String payload=new com.fasterxml.jackson.databind.ObjectMapper().findAndRegisterModules().writeValueAsString(paidEvent);
                    jdbc.update("INSERT INTO booking.bill_paid_deliveries(bill_id,payload) VALUES (?,?) ON CONFLICT DO NOTHING",bill.getId(),payload);
                } catch(com.fasterxml.jackson.core.JsonProcessingException e) {
                    throw new IllegalStateException("Cannot persist bill receipt event",e);
                }
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
