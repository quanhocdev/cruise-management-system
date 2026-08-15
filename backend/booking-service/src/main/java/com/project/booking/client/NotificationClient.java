package com.project.booking.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NotificationClient {
    private final RestClient client; private final String apiKey;
    public NotificationClient(RestClient.Builder builder,
        @Value("${notification-service.url:http://notification-service:8086}") String baseUrl,
        @Value("${internal.api-key}") String apiKey) {
        this.client = builder.baseUrl(baseUrl).build(); this.apiKey = apiKey;
    }
    public boolean send(Long userId, String email, String type, String title, String message, Long bookingId) {
        try {
            client.post().uri("/internal/notifications").header("X-Internal-Api-Key", apiKey)
                .body(new Event(userId, email, type, title, message, "BOOKING", bookingId))
                .retrieve().toBodilessEntity();
            return true;
        } catch (RuntimeException ignored) {
            // Notification is best-effort and must never roll back the booking transaction.
            return false;
        }
    }
    private record Event(Long recipientUserId, String recipientEmail, String type, String title,
                         String message, String referenceType, Long referenceId) {}
}
