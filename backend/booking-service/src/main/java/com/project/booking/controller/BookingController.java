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

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {
    private final BookingService service;
    public BookingController(BookingService service) { this.service = service; }

    @PostMapping
    ResponseEntity<BookingResponse> create(@Valid @RequestBody CreateBookingRequest request, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, userId(jwt)));
    }
    @GetMapping("/me")
    List<BookingResponse> mine(@AuthenticationPrincipal Jwt jwt) { return service.getMine(userId(jwt)); }
    @GetMapping("/{id}")
    BookingResponse get(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt, Authentication authentication) {
        boolean privileged = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return service.get(id, userId(jwt), privileged);
    }
    @PatchMapping("/{id}/cancel")
    BookingResponse cancel(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.cancel(id, userId(jwt));
    }
    private Long userId(Jwt jwt) {
        Object claim = jwt.getClaim("userId");
        if (claim instanceof Number number) return number.longValue();
        try { return Long.valueOf(String.valueOf(claim)); }
        catch (Exception ex) { throw new BookingException(HttpStatus.BAD_REQUEST, "JWT userId claim is missing or invalid"); }
    }
}
