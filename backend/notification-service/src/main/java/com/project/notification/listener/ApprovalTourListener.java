package com.project.notification.listener;

import com.project.common.event.TourApprovedEvent;
import com.project.common.event.TourAssignmentEvent;
import com.project.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class ApprovalTourListener {

    private static final Logger log = LoggerFactory.getLogger(ApprovalTourListener.class);

    private final NotificationService notificationService;

    public ApprovalTourListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "tour-approved-topic", groupId = "notification-service-group")
    public void onTourApproved(
            TourApprovedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key) {

        log.info("==> [Notification Listener] Nhận TourApprovedEvent - Partition: {}, Offset: {}, Tour ID: {}",
                partition, offset, event.tourId());

        if (event.assignments() == null || event.assignments().isEmpty()) {
            log.info("==> [Notification Listener] Tour {} không có assignment nào để thông báo.", event.tourId());
            return;
        }

        notificationService.processTourApproved(event);
    }
}