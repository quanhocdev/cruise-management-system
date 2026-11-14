package com.project.booking.controller.finance;

import com.project.booking.dto.refund.BookingRefundResponse;
import com.project.booking.dto.refund.FinanceRefundRejectRequest;
import com.project.booking.exception.AppException;
import com.project.booking.model.enums.BookingRefundStatus;
import com.project.booking.service.finance.refund.FinanceBookingRefundService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/finance/refunds")
@PreAuthorize("hasAnyRole('FINANCE', 'ADMIN')")
public class FinanceBookingRefundController {

    private final FinanceBookingRefundService refundService;

    public FinanceBookingRefundController(
            FinanceBookingRefundService refundService) {
        this.refundService = refundService;
    }

    /**
     * Danh sách yêu cầu hoàn tiền.
     * Mặc định chỉ lấy PENDING.
     */
    @GetMapping
    public ResponseEntity<List<BookingRefundResponse>> getRefunds(
            @RequestParam(required = false) BookingRefundStatus status) {

        return ResponseEntity.ok(
                refundService.getRefunds(status));
    }

    /**
     * Chi tiết một yêu cầu hoàn tiền.
     */
    @GetMapping("/{refundId}")
    public ResponseEntity<BookingRefundResponse> getRefundDetail(
            @PathVariable Long refundId) {

        return ResponseEntity.ok(
                refundService.getRefundDetail(refundId));
    }

    /**
     * Finance xác nhận hoàn tiền.
     * Đây là thời điểm Booking Service phát event sang Payment Service.
     */
    @PostMapping("/{refundId}/approve")
    public ResponseEntity<BookingRefundResponse> approveRefund(
            @PathVariable Long refundId,
            Authentication authentication) {

        UUID reviewerId = getCurrentUserId(authentication);

        return ResponseEntity.ok(
                refundService.approveRefund(refundId, reviewerId));
    }

    /**
     * Finance từ chối yêu cầu hoàn tiền.
     */
    @PostMapping("/{refundId}/reject")
    public ResponseEntity<BookingRefundResponse> rejectRefund(
            @PathVariable Long refundId,
            @Valid @RequestBody FinanceRefundRejectRequest request,
            Authentication authentication) {

        UUID reviewerId = getCurrentUserId(authentication);

        return ResponseEntity.ok(
                refundService.rejectRefund(
                        refundId,
                        reviewerId,
                        request));
    }

    private UUID getCurrentUserId(Authentication authentication) {

        if (authentication == null
                || authentication.getName() == null
                || authentication.getName().isBlank()) {

            throw new AppException(
                    "Unauthenticated user",
                    HttpStatus.UNAUTHORIZED);
        }

        try {
            return UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException exception) {
            throw new AppException(
                    "Invalid authenticated user ID",
                    HttpStatus.UNAUTHORIZED);
        }
    }
}