package com.project.notification.service;

import com.project.notification.dto.*;
import java.util.List;

public interface TestNotificationService {
    NotificationResponse create(CreateNotificationRequest request);

    List<NotificationResponse> getMine(Long userId);

    UnreadCountResponse unreadCount(Long userId);

    NotificationResponse markRead(Long id, Long userId);

    void markAllRead(Long userId);
}
