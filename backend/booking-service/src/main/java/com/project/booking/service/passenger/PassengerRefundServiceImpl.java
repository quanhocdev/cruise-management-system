package com.project.booking.service.passenger;

import com.project.booking.client.TourRefundClient;
import com.project.booking.dto.refund.BookingRefundRequest;
import com.project.booking.dto.refund.BookingRefundResponse;
import com.project.booking.exception.AppException;
import com.project.booking.mapper.BookingRefundMapper;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingRefund;
import com.project.booking.model.enums.BookingRefundStatus;
import com.project.booking.model.enums.BookingStatus;
import com.project.booking.repository.BookingRefundRepository;
import com.project.booking.repository.BookingRepository;
import com.project.booking.service.passenger.PassengerRefundService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.booking.dto.refund.BookingRefundPreviewResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PassengerRefundServiceImpl implements PassengerRefundService {

        private static final List<BookingRefundStatus> ACTIVE_REFUND_STATUSES = List.of(
                        BookingRefundStatus.PENDING,
                        BookingRefundStatus.APPROVED,
                        BookingRefundStatus.PROCESSING);

        private final BookingRepository bookingRepository;
        private final BookingRefundRepository bookingRefundRepository;
        private final BookingRefundMapper bookingRefundMapper;
        private final TourRefundClient tourRefundClient;

        public PassengerRefundServiceImpl(
                        BookingRepository bookingRepository,
                        BookingRefundRepository bookingRefundRepository,
                        BookingRefundMapper bookingRefundMapper,
                        TourRefundClient tourRefundClient) {
                this.bookingRepository = bookingRepository;
                this.bookingRefundRepository = bookingRefundRepository;
                this.bookingRefundMapper = bookingRefundMapper;
                this.tourRefundClient = tourRefundClient;
        }

        @Override
        public BookingRefundResponse create(
                        Long bookingId,
                        UUID userId,
                        BookingRefundRequest request) {
                // 1. Tìm booking
                Booking booking = findBooking(bookingId);

                // 2. Kiểm tra quyền sở hữu
                if (!userId.equals(booking.getCreatedByUserId())) {
                        throw new AppException(
                                        "You cannot request a refund for this booking",
                                        HttpStatus.FORBIDDEN);
                }

                // 3. Chỉ booking đã thanh toán mới được yêu cầu hoàn tiền
                if (booking.getStatus() != BookingStatus.CONFIRMED) {
                        throw new AppException(
                                        "Only CONFIRMED bookings can request a refund",
                                        HttpStatus.CONFLICT);
                }

                // 4. Kiểm tra yêu cầu hoàn tiền đang tồn tại
                boolean hasActiveRefund = bookingRefundRepository
                                .existsByBookingIdAndStatusIn(
                                                bookingId,
                                                ACTIVE_REFUND_STATUSES);

                if (hasActiveRefund) {
                        throw new AppException(
                                        "An active refund request already exists for this booking",
                                        HttpStatus.CONFLICT);
                }

                // 5. Lấy kết quả tính hoàn tiền từ tour-service
                TourRefundClient.RefundCalculationInfo calculation = tourRefundClient.calculateRefund(
                                booking.getTourId(),
                                booking.getTotalAmount());

                if (calculation == null
                                || calculation.refundPercentage() == null
                                || calculation.calculatedRefundAmount() == null
                                || calculation.departureDate() == null
                                || calculation.daysBeforeDeparture() == null) {
                        throw new AppException(
                                        "Unable to calculate refund information",
                                        HttpStatus.BAD_GATEWAY);
                }

                BigDecimal refundAmount = calculation.calculatedRefundAmount();

                if (refundAmount.compareTo(BigDecimal.ZERO) < 0
                                || refundAmount.compareTo(booking.getTotalAmount()) > 0) {
                        throw new AppException(
                                        "Invalid refund amount returned by tour service",
                                        HttpStatus.BAD_GATEWAY);
                }

                // 6. Tạo yêu cầu hoàn tiền và lưu snapshot chính sách
                BookingRefund refund = new BookingRefund();

                refund.setBookingId(booking.getId());
                refund.setRequestedByUserId(userId);
                refund.setReason(
                                request != null && request.reason() != null
                                                ? request.reason().trim()
                                                : null);

                refund.setPolicyRefundRuleId(calculation.policyRefundRuleId());
                refund.setDepartureDate(calculation.departureDate());
                refund.setDaysBeforeDeparture(calculation.daysBeforeDeparture());

                refund.setOriginalAmount(booking.getTotalAmount());
                refund.setRefundPercentage(calculation.refundPercentage());
                refund.setCalculatedRefundAmount(refundAmount);

                refund.setStatus(BookingRefundStatus.PENDING);

                BookingRefund savedRefund = bookingRefundRepository.save(refund);

                // 7. Cập nhật trạng thái booking
                booking.setStatus(BookingStatus.PENDING_REFUND);
                bookingRepository.save(booking);

                return bookingRefundMapper.toResponse(savedRefund);
        }

        @Override
        @Transactional(readOnly = true)
        public BookingRefundResponse getLatest(
                        Long bookingId,
                        UUID userId) {
                Booking booking = findBooking(bookingId);

                if (!userId.equals(booking.getCreatedByUserId())) {
                        throw new AppException(
                                        "You cannot access this refund request",
                                        HttpStatus.FORBIDDEN);
                }

                BookingRefund refund = bookingRefundRepository
                                .findFirstByBookingIdOrderByCreatedAtDesc(bookingId)
                                .orElseThrow(() -> new AppException(
                                                "Refund request not found",
                                                HttpStatus.NOT_FOUND));

                return bookingRefundMapper.toResponse(refund);
        }

        @Override
        @Transactional(readOnly = true)
        public List<BookingRefundResponse> getMine(UUID userId) {
                return bookingRefundRepository
                                .findByRequestedByUserIdOrderByCreatedAtDesc(userId)
                                .stream()
                                .map(bookingRefundMapper::toResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public BookingRefundPreviewResponse preview(
                        Long bookingId,
                        UUID userId) {

                // 1. Tìm booking
                Booking booking = findBooking(bookingId);

                // 2. Kiểm tra quyền sở hữu
                if (!userId.equals(booking.getCreatedByUserId())) {
                        throw new AppException(
                                        "You cannot preview a refund for this booking",
                                        HttpStatus.FORBIDDEN);
                }

                // 3. Chỉ booking đã thanh toán mới được xem trước hoàn tiền
                if (booking.getStatus() != BookingStatus.CONFIRMED) {
                        throw new AppException(
                                        "Only CONFIRMED bookings can preview a refund",
                                        HttpStatus.CONFLICT);
                }

                // 4. Không cho xem trước nếu đã có yêu cầu hoàn tiền đang xử lý
                boolean hasActiveRefund = bookingRefundRepository
                                .existsByBookingIdAndStatusIn(
                                                bookingId,
                                                ACTIVE_REFUND_STATUSES);

                if (hasActiveRefund) {
                        throw new AppException(
                                        "An active refund request already exists for this booking",
                                        HttpStatus.CONFLICT);
                }

                // 5. Gọi tour-service tính toán
                TourRefundClient.RefundCalculationInfo calculation = tourRefundClient.calculateRefund(
                                booking.getTourId(),
                                booking.getTotalAmount());

                if (calculation == null
                                || calculation.policyRefundRuleId() == null
                                || calculation.refundPercentage() == null
                                || calculation.calculatedRefundAmount() == null
                                || calculation.departureDate() == null
                                || calculation.daysBeforeDeparture() == null) {
                        throw new AppException(
                                        "Unable to calculate refund information",
                                        HttpStatus.BAD_GATEWAY);
                }

                BigDecimal refundAmount = calculation.calculatedRefundAmount();

                if (refundAmount.compareTo(BigDecimal.ZERO) < 0
                                || refundAmount.compareTo(booking.getTotalAmount()) > 0) {
                        throw new AppException(
                                        "Invalid refund amount returned by tour service",
                                        HttpStatus.BAD_GATEWAY);
                }

                // 6. Trả kết quả xem trước, không lưu DB
                return new BookingRefundPreviewResponse(
                                calculation.policyRefundRuleId(),
                                calculation.departureDate(),
                                calculation.daysBeforeDeparture(),
                                booking.getTotalAmount(),
                                calculation.refundPercentage(),
                                refundAmount);
        }

        private Booking findBooking(Long bookingId) {
                return bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new AppException(
                                                "Booking not found: " + bookingId,
                                                HttpStatus.NOT_FOUND));
        }
}