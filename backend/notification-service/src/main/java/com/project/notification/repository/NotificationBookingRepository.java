package com.project.notification.repository;

import com.project.notification.model.NotificationBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationBookingRepository extends JpaRepository<NotificationBooking, Long> {
    List<NotificationBooking> findAllByRecipientUserIdOrderByCreatedAtDesc(Long userId);

    long countByRecipientUserIdAndReadAtIsNull(Long userId);

    List<NotificationBooking> findAllByRecipientUserIdAndReadAtIsNull(Long userId);
}
