package com.project.booking.controller.passenger;

import com.project.booking.dto.refund.BookingRefundPreviewResponse;
import com.project.booking.dto.refund.BookingRefundRequest;
import com.project.booking.dto.refund.BookingRefundResponse;
import com.project.booking.service.passenger.PassengerRefundService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/passengers")
public class PassengerBookingRefundController {

    private final PassengerRefundService passengerRefundService;

    public PassengerBookingRefundController(
            PassengerRefundService passengerRefundService) {
        this.passengerRefundService = passengerRefundService;
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

    /**
     * Xem trước thông tin hoàn tiền.
     */
    @GetMapping("/bookings/{bookingId}/refund-preview")
    public ResponseEntity<BookingRefundPreviewResponse> preview(
            @PathVariable Long bookingId,
            Principal principal) {

        UUID userId = extractUserId(principal);

        return ResponseEntity.ok(
                passengerRefundService.preview(bookingId, userId));
    }

    /**
     * Gửi yêu cầu hoàn tiền.
     */
    @PostMapping("/bookings/{bookingId}/refunds")
    public ResponseEntity<BookingRefundResponse> create(
            @PathVariable Long bookingId,
            @RequestBody BookingRefundRequest request,
            Principal principal) {

        UUID userId = extractUserId(principal);

        BookingRefundResponse response = passengerRefundService.create(bookingId, userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Xem yêu cầu hoàn tiền gần nhất của booking.
     */
    @GetMapping("/bookings/{bookingId}/refunds/latest")
    public ResponseEntity<BookingRefundResponse> getLatest(
            @PathVariable Long bookingId,
            Principal principal) {

        UUID userId = extractUserId(principal);

        return ResponseEntity.ok(
                passengerRefundService.getLatest(bookingId, userId));
    }

    /**
     * Xem lịch sử yêu cầu hoàn tiền của Passenger.
     */
    @GetMapping("/refunds")
    public ResponseEntity<List<BookingRefundResponse>> getMine(
            Principal principal) {

        UUID userId = extractUserId(principal);

        return ResponseEntity.ok(
                passengerRefundService.getMine(userId));
    }
}