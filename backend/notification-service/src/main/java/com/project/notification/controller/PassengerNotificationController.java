
package com.project.notification.controller;

import com.project.notification.dto.response.NotificationResponse;
import com.project.notification.service.NotificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/passenger/notifications")
public class PassengerNotificationController {

    private final NotificationService notificationService;

    public PassengerNotificationController(
            NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Lấy toàn bộ thông báo của Passenger.
     */
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(
            @AuthenticationPrincipal Jwt jwt) {
        UUID passengerId = getUserId(jwt);

        return ResponseEntity.ok(
                notificationService.getAllNotifications(passengerId));
    }

    /**
     * Lấy số lượng thông báo chưa đọc.
     */
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> countUnreadNotifications(
            @AuthenticationPrincipal Jwt jwt) {
        UUID passengerId = getUserId(jwt);

        long unreadCount = notificationService.countUnreadNotifications(passengerId);

        return ResponseEntity.ok(
                Map.of("unreadCount", unreadCount));
    }

    /**
     * Đánh dấu một thông báo đã đọc.
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        UUID passengerId = getUserId(jwt);

        return ResponseEntity.ok(
                notificationService.markAsRead(id, passengerId));
    }

    /**
     * Đánh dấu tất cả thông báo đã đọc.
     */
    @PatchMapping("/read-all")
    public ResponseEntity<Map<String, Integer>> markAllAsRead(
            @AuthenticationPrincipal Jwt jwt) {
        UUID passengerId = getUserId(jwt);

        int updatedCount = notificationService.markAllAsRead(passengerId);

        return ResponseEntity.ok(
                Map.of("updatedCount", updatedCount));
    }

    /**
     * Lấy UUID người dùng từ JWT.
     */
    private UUID getUserId(Jwt jwt) {
        String userId = jwt.getClaimAsString("userId");

        if (userId == null || userId.isBlank()) {
            userId = jwt.getSubject();
        }

        return UUID.fromString(userId);
    }
}
