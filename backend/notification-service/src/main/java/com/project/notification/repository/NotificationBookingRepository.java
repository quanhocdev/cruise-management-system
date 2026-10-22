
package com.project.notification.repository;

import com.project.notification.model.NotificationBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationBookingRepository
        extends JpaRepository<NotificationBooking, Long> {

    List<NotificationBooking> findAllByRecipientUserIdOrderByCreatedAtDesc(UUID userId);

    long countByRecipientUserIdAndReadAtIsNull(UUID userId);

    List<NotificationBooking> findAllByRecipientUserIdAndReadAtIsNull(UUID userId);
}
