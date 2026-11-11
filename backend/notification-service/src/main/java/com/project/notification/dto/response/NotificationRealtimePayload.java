
package com.project.notification.dto.response;

public record NotificationRealtimePayload(
        String eventType,
        NotificationResponse notification,
        long unreadCount) {
}
