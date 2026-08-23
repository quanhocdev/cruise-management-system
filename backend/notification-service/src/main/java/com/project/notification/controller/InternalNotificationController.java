package com.project.notification.controller;

import com.project.notification.dto.*;
import com.project.notification.exception.NotificationException;
import com.project.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/internal/notifications")
public class InternalNotificationController {
    private final NotificationService service;
    private final byte[] expectedApiKey;
    public InternalNotificationController(NotificationService service, @Value("${internal.api-key}") String apiKey) {
        this.service = service; this.expectedApiKey = apiKey.getBytes(StandardCharsets.UTF_8);
    }
    @PostMapping
    ResponseEntity<NotificationResponse> create(@RequestHeader(value = "X-Internal-Api-Key", required = false) String apiKey,
                                                 @Valid @RequestBody CreateNotificationRequest request) {
        authorize(apiKey); return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }
    private void authorize(String apiKey) {
        byte[] supplied = apiKey == null ? new byte[0] : apiKey.getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(expectedApiKey, supplied))
            throw new NotificationException(HttpStatus.UNAUTHORIZED, "Invalid internal API key");
    }
}
