package com.project.activityvisit.listener;

import com.project.activityvisit.service.VisitTourService;
import com.project.common.event.TourMasterSyncEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class ActivityVisitMasterSyncListener {

    private static final Logger log = LoggerFactory.getLogger(ActivityVisitMasterSyncListener.class);
    private final VisitTourService visitTourService;

    public ActivityVisitMasterSyncListener(VisitTourService visitTourService) {
        this.visitTourService = visitTourService;
    }

    @KafkaListener(topics = "tour-master-sync-topic", groupId = "activity-visit-master-group-v1")
    public void onTourMasterSync(
            TourMasterSyncEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("==> [Kafka Consumer] Nhận TourMasterSyncEvent cho Visit - Tour ID: {}, Partition: {}, Offset: {}",
                event.tourId(), partition, offset);

        try {
            visitTourService.syncTourMasterData(event);
            log.info("==> [Activity Visit] Đồng bộ Master Data thành công cho Tour ID: {}", event.tourId());
        } catch (Exception e) {
            log.error("==> [Activity Visit] Lỗi xử lý TourMasterSyncEvent cho Tour ID: {}", event.tourId(), e);
            throw e;
        }
    }
}