package com.project.booking.controller;

import com.project.booking.dto.*;
import com.project.booking.exception.BookingException;
import com.project.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {
    private final BookingService service;
    private final com.project.booking.service.QrCodeService qrCodeService;
    public BookingController(BookingService service, com.project.booking.service.QrCodeService qrCodeService) {
        this.service = service; this.qrCodeService = qrCodeService;
    }

    @PostMapping
    ResponseEntity<BookingResponse> create(@Valid @RequestBody CreateBookingRequest request, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, userId(jwt)));
    }
    @GetMapping("/me")
    List<BookingResponse> mine(@AuthenticationPrincipal Jwt jwt) { return service.getMine(userId(jwt)); }
    @GetMapping("/voyages/{voyageId}/available-rooms")
    List<AvailableRoomResponse> availableRooms(@PathVariable UUID voyageId) {
        return service.getAvailableRooms(voyageId);
    }
    @GetMapping("/{id}")
    BookingResponse get(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt, Authentication authentication) {
        boolean privileged = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return service.get(id, userId(jwt), privileged);
    }
    @PatchMapping("/{id}/cancel")
    BookingResponse cancel(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.cancel(id, userId(jwt));
    }
    @GetMapping("/code/{bookingCode}")
    BookingResponse getByCode(@PathVariable String bookingCode, @AuthenticationPrincipal Jwt jwt,
                              Authentication authentication) {
        return service.getByCode(bookingCode, userId(jwt), privileged(authentication));
    }
    @GetMapping(value = "/{id}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    ResponseEntity<byte[]> qr(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt,
                              Authentication authentication) {
        BookingResponse booking = service.get(id, userId(jwt), privileged(authentication));
        if (booking.bookingCode() == null)
            throw new BookingException(HttpStatus.CONFLICT, "QR code is available after successful payment");
        return ResponseEntity.ok()
            .cacheControl(CacheControl.noStore())
            .body(qrCodeService.png(booking.bookingCode()));
    }
    private boolean privileged(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(a ->
            a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SCHEDULE"));
    }
    private Long userId(Jwt jwt) {
        Object claim = jwt.getClaim("userId");
        if (claim instanceof Number number) return number.longValue();
        try { return Long.valueOf(String.valueOf(claim)); }
        catch (Exception ex) { throw new BookingException(HttpStatus.BAD_REQUEST, "JWT userId claim is missing or invalid"); }
    }
}
