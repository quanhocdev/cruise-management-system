package com.project.notification.service;

import com.project.notification.dto.*;
import com.project.notification.exception.NotificationException;
import com.project.notification.model.*;
import com.project.notification.repository.NotificationRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTests {
    @Mock NotificationRepository repository;
    @Mock EmailDeliveryService emailDeliveryService;
    NotificationServiceImpl service;
    @BeforeEach void setup() { service = new NotificationServiceImpl(repository, emailDeliveryService); }

    @Test void internalEventIsStoredAndEmailDeliveryIsBestEffort() {
        when(repository.save(any())).thenAnswer(i -> { Notification n = i.getArgument(0); n.setId(1L); return n; });
        NotificationResponse result = service.create(request());
        assertEquals(7L, result.recipientUserId()); assertEquals(EmailStatus.PENDING, result.emailStatus());
        verify(emailDeliveryService).deliver(any(Notification.class));
    }

    @Test void userCannotReadAnotherUsersNotification() {
        Notification n = notification(); when(repository.findById(1L)).thenReturn(Optional.of(n));
        assertThrows(NotificationException.class, () -> service.markRead(1L, 99L));
    }

    @Test void markAllReadOnlyUpdatesCurrentUsersUnreadItems() {
        Notification n = notification(); when(repository.findAllByRecipientUserIdAndReadAtIsNull(7L)).thenReturn(List.of(n));
        service.markAllRead(7L); assertNotNull(n.getReadAt()); verify(repository).saveAll(List.of(n));
    }

    private CreateNotificationRequest request() { return new CreateNotificationRequest(7L, "a@example.com",
        NotificationType.PAYMENT_SUCCESS, "Paid", "Payment completed", "BOOKING", 10L); }
    private Notification notification() { Notification n = new Notification(); n.setId(1L); n.setRecipientUserId(7L);
        n.setType(NotificationType.PAYMENT_SUCCESS); n.setTitle("Paid"); n.setMessage("Done");
        n.setEmailStatus(EmailStatus.NOT_REQUESTED); n.setCreatedAt(java.time.Instant.now()); return n; }
}
