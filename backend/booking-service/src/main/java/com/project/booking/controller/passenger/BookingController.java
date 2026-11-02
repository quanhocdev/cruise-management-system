
package com.project.booking.controller.passenger;

import com.project.booking.dto.booking.BookingResponse;
import com.project.booking.dto.booking.CreateBookingRequest;
import com.project.booking.service.passenger.BookingService;
import com.project.booking.model.enums.BookingStatus;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import org.springframework.http.HttpStatus;
import org.springframework.http.CacheControl;
import org.springframework.web.server.ResponseStatusException;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/passengers/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    private UUID extractUserId(Principal principal) {
        if (principal instanceof JwtAuthenticationToken jwtAuth) {
            // Lấy userId từ claim trong JWT
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

        // Fallback nếu principal.getName() lưu userId dưới dạng UUID
        try {
            return UUID.fromString(principal.getName());
        } catch (Exception e) {
            throw new RuntimeException(
                    "Không thể xác định userId dạng UUID từ token xác thực.");
        }
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getMine(Principal principal) {
        UUID userId = extractUserId(principal);
        return ResponseEntity.ok(bookingService.getMine(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getById(
            @PathVariable Long id,
            Principal principal,
            @RequestParam(defaultValue = "false") boolean privileged) {

        UUID userId = extractUserId(principal);
        boolean staffAccess = privileged && principal instanceof JwtAuthenticationToken jwtAuth
                && jwtAuth.getAuthorities().stream().anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_FINANCE")
                                || authority.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(bookingService.get(id, userId, staffAccess));
    }

    @GetMapping(value = "/{id}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQr(@PathVariable Long id, Principal principal)
            throws WriterException, IOException {
        var booking = bookingService.get(id, extractUserId(principal), false);
        if (booking.status() != BookingStatus.CONFIRMED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Booking must be confirmed before showing its QR");
        }
        if (booking.bookingCode() == null || booking.bookingCode().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Booking code is not available");
        }
        var matrix = new QRCodeWriter().encode(
                booking.bookingCode(), BarcodeFormat.QR_CODE, 320, 320);
        var output = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", output);
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .contentType(MediaType.IMAGE_PNG).body(output.toByteArray());
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @ModelAttribute CreateBookingRequest request,
            Principal principal) {

        UUID userId = extractUserId(principal);
        String email = extractEmail(principal);

        System.out.println("==========================================");
        System.out.println(">>> [CONTROLLER] ĐÃ NHẬN ĐƯỢC REQUEST ĐẶT VÉ!");
        System.out.println(">>> User ID từ Token: " + userId);
        System.out.println(">>> Email từ Token: " + email);
        System.out.println("==========================================");

        return ResponseEntity.ok(
                bookingService.create(request, userId, email));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long id,
            Principal principal) {

        UUID userId = extractUserId(principal);
        return ResponseEntity.ok(bookingService.cancel(id, userId));
    }
}
