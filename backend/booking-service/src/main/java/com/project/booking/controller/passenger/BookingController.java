
package com.project.booking.controller.passenger;

import com.project.booking.dto.booking.BookingResponse;
import com.project.booking.dto.booking.CreateBookingRequest;
import com.project.booking.dto.booking.PassengerBookedTourResponse;
import com.project.booking.dto.booking.PassengerTourTrackingResponse;
import com.project.booking.service.passenger.BookingService;
import com.project.booking.service.queue.BookingQueueManager;
import com.project.booking.service.queue.BookingQueueTask;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/passengers/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingQueueManager bookingQueueManager;

    public BookingController(
            BookingService bookingService,
            BookingQueueManager bookingQueueManager) {

        this.bookingService = bookingService;
        this.bookingQueueManager = bookingQueueManager;
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

    private String extractEmail(Principal principal) {

        if (principal instanceof JwtAuthenticationToken jwtAuth) {

            Object emailClaim = jwtAuth.getTokenAttributes().get("email");

            if (emailClaim != null) {
                return emailClaim.toString();
            }
        }

        throw new RuntimeException(
                "Không thể xác định email từ token xác thực.");
    }

    @GetMapping("/tours")
    public ResponseEntity<List<PassengerBookedTourResponse>> getMyBookedTours(
            Principal principal) {

        UUID userId = extractUserId(principal);

        return ResponseEntity.ok(
                bookingService.getMyBookedTours(userId));
    }

    @GetMapping("/tours/{tourId}/details")
    public ResponseEntity<PassengerTourTrackingResponse> getMyBookedTourDetails(
            @PathVariable UUID tourId,
            Principal principal) {

        UUID userId = extractUserId(principal);

        return ResponseEntity.ok(
                bookingService.getMyBookedTourDetails(userId, tourId));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getMine(
            Principal principal) {

        UUID userId = extractUserId(principal);

        return ResponseEntity.ok(
                bookingService.getMine(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getById(
            @PathVariable Long id,
            Principal principal,
            @RequestParam(defaultValue = "false") boolean privileged) {

        UUID userId = extractUserId(principal);

        return ResponseEntity.ok(
                bookingService.get(id, userId, privileged));
    }

    /**
     * Tạo booking thông qua bounded FIFO Queue.
     *
     * HTTP request sẽ chờ cho đến khi Worker xử lý xong
     * và trả kết quả thực tế của booking.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookingResponse> create(
            @ModelAttribute CreateBookingRequest request,
            Principal principal) throws IOException {

        UUID userId = extractUserId(principal);
        String email = extractEmail(principal);
        long reqStart = System.nanoTime();

        System.out.println(
                ">>> [BOOKING QUEUE] RECEIVED REQUEST");

        System.out.println(
                ">>> User ID: " + userId);

        System.out.println(
                ">>> Email: " + email);

        // Đưa request vào Queue.
        // Nếu Queue đầy, QueueFullException sẽ được GlobalExceptionHandler xử lý.
        BookingQueueTask task = bookingQueueManager.submit(
                userId,
                email,
                request);

        System.out.println(
                ">>> [BOOKING QUEUE] ACCEPTED REQUEST ID: "
                        + task.getRequestId());

        try {

            // Chờ đúng kết quả của request này.
            // Không trả HTTP 202 khi mới tiếp nhận.
            BookingResponse response = task.getResultFuture().get();

            System.out.println(">>> [TIMING] TONG tu luc controller nhan: "
                    + (System.nanoTime() - reqStart) / 1_000_000 + " ms");

            return ResponseEntity.ok(response);

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Yêu cầu đặt tour bị gián đoạn. Vui lòng kiểm tra trạng thái đặt tour.");

        } catch (ExecutionException exception) {

            Throwable cause = exception.getCause();

            // Giữ nguyên AppException và các RuntimeException
            // do BookingService ném ra để GlobalExceptionHandler xử lý.
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }

            if (cause instanceof Error error) {
                throw error;
            }

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Không thể xử lý yêu cầu đặt tour.",
                    cause);
        }
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long id,
            Principal principal) {

        UUID userId = extractUserId(principal);

        return ResponseEntity.ok(
                bookingService.cancel(id, userId));
    }
}
