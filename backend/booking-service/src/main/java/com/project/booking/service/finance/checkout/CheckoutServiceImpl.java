
package com.project.booking.service.finance.checkout;

import com.project.booking.dto.finance.CheckoutPreviewResponse;
import com.project.booking.dto.finance.CheckoutPreviewResponse.PassengerCheckoutPreview;
import com.project.booking.dto.finance.CheckoutResponse;
import com.project.booking.dto.payment.BillPaymentRequest;
import com.project.booking.dto.payment.BillPaymentResponse;
import com.project.booking.mapper.BillItemMapper;
import com.project.booking.mapper.CheckoutMapper;
import com.project.booking.model.Bill;
import com.project.booking.model.BillItem;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.model.enums.BillStatus;
import com.project.booking.repository.BillItemRepository;
import com.project.booking.repository.BillRepository;
import com.project.booking.repository.BookingPassengerRepository;
import com.project.booking.repository.BookingRepository;
import com.project.booking.service.finance.bill.PaymentCheckoutService;
import com.project.booking.service.finance.checkout.PassengerUsageReader.PassengerUsages;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.booking.service.account.OnboardAccountService;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CheckoutServiceImpl implements CheckoutService {
        private final org.springframework.transaction.support.TransactionTemplate transaction;
        private final org.springframework.jdbc.core.JdbcTemplate jdbc;

        private final OnboardAccountService accounts;
        private final com.project.booking.service.account.LedgerAdjustmentReader adjustments;
    private final BookingRepository bookingRepository;
        private final BookingPassengerRepository bookingPassengerRepository;
        private final BillRepository billRepository;
        private final BillItemRepository billItemRepository;
        private final CheckoutMapper checkoutMapper;
        private final BillItemMapper billItemMapper;
        private final PaymentCheckoutService paymentCheckoutService;
        private final PassengerUsageReader passengerUsageReader;
        private final PassengerCheckoutPreviewBuilder passengerCheckoutPreviewBuilder;
        private final BillCodeGenerator billCodeGenerator;

        public CheckoutServiceImpl(
                        BookingRepository bookingRepository,
                        BookingPassengerRepository bookingPassengerRepository,
                        BillRepository billRepository,
                        BillItemRepository billItemRepository,
                        CheckoutMapper checkoutMapper,
                        BillItemMapper billItemMapper,
                        PaymentCheckoutService paymentCheckoutService,
                        PassengerUsageReader passengerUsageReader,
                        PassengerCheckoutPreviewBuilder passengerCheckoutPreviewBuilder,
                        BillCodeGenerator billCodeGenerator, OnboardAccountService accounts,
                        com.project.booking.service.account.LedgerAdjustmentReader adjustments,
                        org.springframework.transaction.PlatformTransactionManager transactionManager,
                        org.springframework.jdbc.core.JdbcTemplate jdbc) {

                this.transaction = new org.springframework.transaction.support.TransactionTemplate(transactionManager);
                this.jdbc = jdbc;

                this.accounts = accounts;
                this.adjustments = adjustments;
        this.bookingRepository = bookingRepository;
                this.bookingPassengerRepository = bookingPassengerRepository;
                this.billRepository = billRepository;
                this.billItemRepository = billItemRepository;
                this.checkoutMapper = checkoutMapper;
                this.billItemMapper = billItemMapper;
                this.paymentCheckoutService = paymentCheckoutService;
                this.passengerUsageReader = passengerUsageReader;
                this.passengerCheckoutPreviewBuilder = passengerCheckoutPreviewBuilder;
                this.billCodeGenerator = billCodeGenerator;
        }

        @Override
        @Transactional(readOnly = true, isolation = org.springframework.transaction.annotation.Isolation.REPEATABLE_READ)
        public CheckoutPreviewResponse getCheckoutPreview(Long bookingId) {

                Booking booking = getBooking(bookingId);

                List<PassengerCheckoutPreview> passengers = bookingPassengerRepository
                                .findAllByBooking_IdOrderByIdAsc(bookingId)
                                .stream()
                                .map(passengerCheckoutPreviewBuilder::build)
                                .toList();

                BigDecimal grandTotal = passengers.stream()
                                .map(PassengerCheckoutPreview::passengerTotal)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                return new CheckoutPreviewResponse(
                                booking.getId(),
                                booking.getBookingCode(),
                                booking.getPrimaryContactName(),
                                booking.getPrimaryContactEmail(),
                                passengers,
                                grandTotal);
        }

        @Override
        @Transactional(propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED)
        public CheckoutResponse confirmCheckout(Long bookingId) {
                CheckoutResponse snapshot = transaction.execute(s -> prepareCheckout(bookingId, null));
                if (snapshot.totalAmount().signum() == 0) return snapshot;
                Long payer = jdbc.queryForObject("select created_by_user_id from booking.bookings where id=?",Long.class,bookingId);
                BillPaymentResponse payment = paymentCheckoutService.createPayment(new BillPaymentRequest(snapshot.billId(),payer,snapshot.totalAmount()));
                return new CheckoutResponse(snapshot.billId(),snapshot.billCode(),snapshot.bookingId(),snapshot.totalAmount(),snapshot.createdAt(),snapshot.items(),payment.paymentId(),payment.paymentUrl(),payment.status());
        }

        @Override
        @Transactional
        public CheckoutResponse prepareCheckout(Long bookingId, String previewVersion) {

                Booking booking = getBooking(bookingId);
                if (booking.getStatus() != com.project.booking.model.enums.BookingStatus.CONFIRMED)
                        throw new com.project.booking.service.command.CommandException("BOOKING_NOT_CONFIRMED","Booking chưa được xác nhận",HttpStatus.UNPROCESSABLE_ENTITY);
                accounts.beginSettlement(booking);

                // 1. Tìm hóa đơn hiện có của booking.
                List<Bill> existingBills = billRepository
                                .findAllByBooking_IdOrderByCreatedAtDesc(bookingId);
                if(existingBills.size()>1)
                        throw new com.project.booking.service.command.CommandException("AMBIGUOUS_BILLS","Booking có nhiều bill, cần kiểm tra trước khi quyết toán",HttpStatus.CONFLICT);

                Bill bill = existingBills.stream()
                                .filter(b -> b.getStatus() == BillStatus.PENDING_PAYMENT)
                                .findFirst()
                                .orElse(null);

                List<BillItem> items;
                Bill finalBill;

                if (bill != null) {
                        if(previewVersion!=null && bill.getPreviewVersion()!=null && !previewVersion.equals(bill.getPreviewVersion()))
                                throw new com.project.booking.service.command.CommandException("PREVIEW_CHANGED","Bill đã chốt với nội dung khác. Vui lòng tải lại.",HttpStatus.UNPROCESSABLE_ENTITY);
                        // 2. Đã có hóa đơn chờ thanh toán: dùng lại hóa đơn và BillItem.
                        finalBill = bill;

                        items = billItemRepository
                                        .findAllByBill_IdOrderByIdAsc(finalBill.getId());

                } else {
                        // 3. Không có hóa đơn chờ thanh toán: kiểm tra hóa đơn đã thanh toán.
                        boolean alreadyPaid = existingBills.stream()
                                        .anyMatch(b -> b.getStatus() == BillStatus.PAID);

                        if (alreadyPaid) {
                                throw new ResponseStatusException(
                                                HttpStatus.CONFLICT,
                                                "Booking này đã có hóa đơn thanh toán thành công.");
                        }

                        var preview = getCheckoutPreview(bookingId);
                        if (previewVersion != null && !previewVersion.equals(preview.previewVersion()))
                                throw new com.project.booking.service.command.CommandException("PREVIEW_CHANGED","Chi tiêu đã thay đổi. Vui lòng cập nhật và xác nhận lại.",HttpStatus.UNPROCESSABLE_ENTITY);
                        if (preview.grandTotal().signum() < 0)
                                throw new com.project.booking.service.command.CommandException("NEGATIVE_BILL","Tổng bill âm, cần kiểm tra điều chỉnh",HttpStatus.UNPROCESSABLE_ENTITY);

                        // 4. Chưa có hóa đơn phù hợp: tạo hóa đơn mới.
                        List<BookingPassenger> bookingPassengers = bookingPassengerRepository
                                        .findAllByBooking_IdOrderByIdAsc(bookingId);
                        if(bookingPassengers.isEmpty() || bookingPassengers.stream().anyMatch(p -> p.getStatus()!=com.project.booking.model.enums.BookingPassengerStatus.CHECKED_IN))
                                throw new com.project.booking.service.command.CommandException("PASSENGERS_NOT_CHECKED_IN","Cần hoàn tất check-in hành khách trước khi quyết toán",HttpStatus.UNPROCESSABLE_ENTITY);

                        Bill newBill = new Bill();
                        newBill.setBooking(booking);
                        newBill.setPreviewVersion(preview.previewVersion());
                        newBill.setBillCode(billCodeGenerator.generate());
                        newBill.setTotalAmount(BigDecimal.ZERO);
                        newBill.setStatus(BillStatus.PENDING_PAYMENT);

                        Bill savedBill = billRepository.save(newBill);

                        items = new ArrayList<>();

                        for (BookingPassenger passenger : bookingPassengers) {
                                var lines = buildBillItems(savedBill, passenger.getId());
                                lines.forEach(line -> line.setPassengerName(passenger.getPassenger().getFullName()));
                                items.addAll(lines);
                        }

                        billItemRepository.saveAll(items);

                        BigDecimal grandTotal = items.stream()
                                        .map(BillItem::getFinalAmount)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        savedBill.setTotalAmount(grandTotal);
                        finalBill = billRepository.save(savedBill);

                        // Lấy lại các BillItem đã lưu để trả response.
                        items = billItemRepository
                                        .findAllByBill_IdOrderByIdAsc(finalBill.getId());
                }

                if (finalBill.getTotalAmount().signum() == 0) {
                        finalBill.setStatus(BillStatus.PAID);
                        billRepository.save(finalBill);
                        accounts.markClosed(bookingId);
                }
                return checkoutMapper.toResponse(finalBill, items);
        }

        private List<BillItem> buildBillItems(
                        Bill bill,
                        Long bookingPassengerId) {

                PassengerUsages usages = passengerUsageReader.read(bookingPassengerId);

                List<BillItem> items = new ArrayList<>();

                usages.activityVisits().forEach(
                                usage -> items.add(
                                                billItemMapper.fromActivityVisit(
                                                                bill, bookingPassengerId, usage)));

                usages.activityCruises().forEach(
                                usage -> items.add(
                                                billItemMapper.fromActivityCruise(
                                                                bill, bookingPassengerId, usage)));

                usages.services().forEach(
                                usage -> items.add(
                                                billItemMapper.fromService(
                                                                bill, bookingPassengerId, usage)));

                usages.products().forEach(
                                usage -> items.add(
                                                billItemMapper.fromProduct(
                                                                bill, bookingPassengerId, usage)));

                items.addAll(adjustments.billItems(bill, bookingPassengerId));
                return items;
        }

        private Booking getBooking(Long bookingId) {
                return bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Booking không tồn tại: " + bookingId));
        }
}
