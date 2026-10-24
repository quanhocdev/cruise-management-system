package com.project.booking.controller.passenger;

import java.util.UUID;

import com.project.booking.service.account.SpendingSummaryService;
import com.project.booking.service.command.CommandException;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/passengers/bookings")
public class SpendingController {
    private final SpendingSummaryService spending;
    public SpendingController(SpendingSummaryService spending) { this.spending = spending; }

    @GetMapping("/{bookingId}/spending")
    public SpendingSummaryService.Summary get(@PathVariable Long bookingId,
            @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="20") int size,
            JwtAuthenticationToken principal) {
        if (principal == null || principal.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_PASSENGER"))) {
            throw new CommandException("PASSENGER_REQUIRED", "Yêu cầu tài khoản hành khách", HttpStatus.FORBIDDEN);
        }
        Object claim = principal.getTokenAttributes().get("userId");
        UUID userId;
        try { userId = UUID.fromString(String.valueOf(claim)); }
        catch (IllegalArgumentException ex) {
            throw new CommandException("INVALID_USER_ID", "Không xác định được tài khoản", HttpStatus.UNAUTHORIZED);
        }
        return spending.getMine(bookingId, userId, page, size);
    }
}
