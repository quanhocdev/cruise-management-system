package com.project.booking.controller.finance;

import com.project.booking.dto.refund.BookingRefundResponse;
import com.project.booking.dto.refund.FinanceRefundRejectRequest;
import com.project.booking.model.enums.BookingRefundStatus;
import com.project.booking.service.finance.refund.FinanceBookingRefundService;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import java.security.Principal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/finance/refunds")
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

    @PostMapping("/{refundId}/approve")
    public ResponseEntity<BookingRefundResponse> approveRefund(
            @PathVariable Long refundId,
            Principal principal) {

        UUID reviewerId = extractUserId(principal);

        return ResponseEntity.ok(
                refundService.approveRefund(refundId, reviewerId));
    }

    @PostMapping("/{refundId}/reject")
    public ResponseEntity<BookingRefundResponse> rejectRefund(
            @PathVariable Long refundId,
            @Valid @RequestBody FinanceRefundRejectRequest request,
            Principal principal) {

        UUID reviewerId = extractUserId(principal);

        return ResponseEntity.ok(
                refundService.rejectRefund(
                        refundId,
                        reviewerId,
                        request));
    }

    private UUID extractUserId(Principal principal) {

        if (principal instanceof JwtAuthenticationToken jwtAuth) {

            Object userIdClaim = jwtAuth.getTokenAttributes().get("userId");

            if (userIdClaim != null) {
                try {
                    return UUID.fromString(userIdClaim.toString());
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(
                            "userId trong token không đúng định dạng UUID.");
                }
            }
        }

        try {
            return UUID.fromString(principal.getName());
        } catch (Exception e) {
            throw new RuntimeException(
                    "Không thể xác định userId dạng UUID từ token xác thực.");
        }
    }
}