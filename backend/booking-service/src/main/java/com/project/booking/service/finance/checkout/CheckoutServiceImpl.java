
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
import com.project.booking.service.PaymentCheckoutService;
import com.project.booking.service.finance.checkout.PassengerUsageReader.PassengerUsages;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CheckoutServiceImpl implements CheckoutService {

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
                        BillCodeGenerator billCodeGenerator) {

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
        @Transactional
        public CheckoutResponse confirmCheckout(Long bookingId) {

                Booking booking = getBooking(bookingId);

                // 1. Tìm hóa đơn hiện có của booking.
                List<Bill> existingBills = billRepository
                                .findAllByBooking_IdOrderByCreatedAtDesc(bookingId);

                Bill bill = existingBills.stream()
                                .filter(b -> b.getStatus() == BillStatus.PENDING_PAYMENT)
                                .findFirst()
                                .orElse(null);

                List<BillItem> items;
                Bill finalBill;

                if (bill != null) {
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

                        // 4. Chưa có hóa đơn phù hợp: tạo hóa đơn mới.
                        List<BookingPassenger> bookingPassengers = bookingPassengerRepository
                                        .findAllByBooking_IdOrderByIdAsc(bookingId);

                        Bill newBill = new Bill();
                        newBill.setBooking(booking);
                        newBill.setBillCode(billCodeGenerator.generate());
                        newBill.setTotalAmount(BigDecimal.ZERO);
                        newBill.setStatus(BillStatus.PENDING_PAYMENT);

                        Bill savedBill = billRepository.save(newBill);

                        items = new ArrayList<>();

                        for (BookingPassenger passenger : bookingPassengers) {
                                items.addAll(buildBillItems(savedBill, passenger.getId()));
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

                // 5. Tạo hoặc tái sử dụng Payment và lấy URL VNPay.
                BillPaymentRequest paymentRequest = new BillPaymentRequest(
                                finalBill.getId(),
                                booking.getCreatedByUserId(),
                                finalBill.getTotalAmount());

                BillPaymentResponse paymentResponse = paymentCheckoutService.createPayment(paymentRequest);

                CheckoutResponse checkoutResponse = checkoutMapper.toResponse(finalBill, items);

                return new CheckoutResponse(
                                checkoutResponse.billId(),
                                checkoutResponse.billCode(),
                                checkoutResponse.bookingId(),
                                checkoutResponse.totalAmount(),
                                checkoutResponse.createdAt(),
                                checkoutResponse.items(),
                                paymentResponse.paymentId(),
                                paymentResponse.paymentUrl(),
                                paymentResponse.status());
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

                return items;
        }

        private Booking getBooking(Long bookingId) {
                return bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Booking không tồn tại: " + bookingId));
        }
}
