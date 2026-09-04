package com.project.notification.controller;

import com.project.notification.dto.*;
import com.project.notification.exception.NotificationException;
import com.project.notification.service.NotificationService;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService service;
    public NotificationController(NotificationService service) { this.service = service; }
    @GetMapping List<NotificationResponse> mine(@AuthenticationPrincipal Jwt jwt) { return service.getMine(userId(jwt)); }
    @GetMapping("/unread-count") UnreadCountResponse unreadCount(@AuthenticationPrincipal Jwt jwt) { return service.unreadCount(userId(jwt)); }
    @PatchMapping("/{id}/read") NotificationResponse markRead(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.markRead(id, userId(jwt));
    }
    @PatchMapping("/read-all") ResponseEntity<Void> markAllRead(@AuthenticationPrincipal Jwt jwt) {
        service.markAllRead(userId(jwt)); return ResponseEntity.noContent().build();
    }
    private Long userId(Jwt jwt) {
        Object claim = jwt.getClaim("userId");
        if (claim instanceof Number number) return number.longValue();
        try { return Long.valueOf(String.valueOf(claim)); }
        catch (Exception ex) { throw new NotificationException(HttpStatus.BAD_REQUEST, "JWT userId claim is missing or invalid"); }
    }
}
