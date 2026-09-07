package com.project.booking.controller;

import com.project.booking.dto.booking.BookingResponse;
import com.project.booking.dto.booking.CreateBookingRequest;
import com.project.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/passengers/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    private Long extractUserId(Principal principal) {
        if (principal instanceof JwtAuthenticationToken jwtAuth) {
            // Lấy trực tiếp userId từ claim trong token (thường lưu dạng Long hoặc String)
            Object userIdClaim = jwtAuth.getTokenAttributes().get("userId"); // hoặc "id", tùy cách bạn định nghĩa lúc
                                                                             // tạo token ở auth-service
            if (userIdClaim != null) {
                return Long.valueOf(userIdClaim.toString());
            }
        }
        // Fallback nếu principal.getName() lưu id dưới dạng chuỗi số
        try {
            return Long.valueOf(principal.getName());
        } catch (Exception e) {
            throw new RuntimeException("Không thể xác định userId từ token xác thực.");
        }
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getMine(Principal principal) {
        Long userId = extractUserId(principal);
        return ResponseEntity.ok(bookingService.getMine(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getById(@PathVariable Long id, Principal principal,
            @RequestParam(defaultValue = "false") boolean privileged) {
        Long userId = extractUserId(principal);
        return ResponseEntity.ok(bookingService.get(id, userId, privileged));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @ModelAttribute CreateBookingRequest request,
            Principal principal) {

        Long userId = extractUserId(principal);

        System.out.println("==========================================");
        System.out.println(">>> [CONTROLLER] ĐÃ NHẬN ĐƯỢC REQUEST ĐẶT VÉ!");
        System.out.println(">>> User ID từ Token: " + userId);
        System.out.println(">>> Tour ID: " + (request != null ? request.getTourId() : "NULL REQUEST"));
        System.out.println(">>> Số lượng hành khách: "
                + (request != null && request.getPassengers() != null ? request.getPassengers().size() : 0));
        System.out.println("==========================================");

        return ResponseEntity.ok(bookingService.create(request, userId));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id, Principal principal) {
        Long userId = extractUserId(principal);
        return ResponseEntity.ok(bookingService.cancel(id, userId));
    }
}