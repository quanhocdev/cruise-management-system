package com.project.tour.service.operation;

import com.project.tour.service.redis.TourRedisService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Chỉ chạy SAU KHI DB commit thành công.
 * Nếu transaction rollback thì listener không chạy -> Redis/Kafka không bị lệch
 * với DB.
 */
@Component
public class TourPackageEventListener {

    private static final Logger log = LoggerFactory.getLogger(TourPackageEventListener.class);

    public static final String TOPIC = "tour-package-sync-topic";

    private final TourRedisService tourRedisService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TourPackageEventListener(
            TourRedisService tourRedisService,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.tourRedisService = tourRedisService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTourPackageChanged(TourPackageChangedEvent event) {

        // 1. Redis
        try {
            if (event.deleted()) {
                tourRedisService.deletePackageAvailableRooms(event.packageId());
            } else if (event.availableRooms() != null) {
                tourRedisService.savePackageAvailableRooms(event.packageId(), event.availableRooms());
            }
        } catch (Exception e) {
            // DB đã commit nên không rollback được -> ghi log để xử lý/đồng bộ lại
            log.error("Failed to update Redis for tour package {}", event.packageId(), e);
        }

        // 2. Kafka
        try {
            kafkaTemplate.send(TOPIC, event.packageId().toString(), event.syncedEvent())
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to send Kafka event for tour package {}", event.packageId(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Failed to send Kafka event for tour package {}", event.packageId(), e);
        }
    }
}