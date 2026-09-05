package com.project.activitycruise.listener;

import com.project.common.event.TourMasterSyncEvent;
import com.project.common.event.TourAssignmentEvent;
import com.project.common.event.enums.TourAssignmentType;
import com.project.activitycruise.service.ActivityCruiseTourService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class ActivityCruiseMasterSyncListener {

    private static final Logger log = LoggerFactory.getLogger(ActivityCruiseMasterSyncListener.class);
    private final ActivityCruiseTourService activityCruiseTourService;

    public ActivityCruiseMasterSyncListener(ActivityCruiseTourService activityCruiseTourService) {
        this.activityCruiseTourService = activityCruiseTourService;
    }

    @KafkaListener(topics = "tour-master-sync-topic", groupId = "activity-cruise-master-group-v1")
    public void onTourMasterSync(
            TourMasterSyncEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("==> [Kafka Consumer] Nhận TourMasterSyncEvent - Tour ID: {}", event.tourId());

        // 1. Bạn có thể lấy thông tin Cruise, Decks, Areas từ event.cruise() để lưu vào
        // DB của activity-cruise-service tại đây
        if (event.cruise() != null) {
            log.info("==> [Activity Cruise] Thông tin du thuyền nhận được: {}", event.cruise().name());
            // TODO: Gọi service lưu Cruise, Deck, Area nội bộ
        }

        // 2. Xử lý các assignments dành riêng cho ACTIVITY_CRUISE
        if (event.assignments() != null) {
            for (TourAssignmentEvent assignment : event.assignments()) {
                if (assignment.type() != TourAssignmentType.ACTIVITY_CRUISE) {
                    continue;
                }

                try {
                    activityCruiseTourService.createActivityTourFromEvent(
                            assignment.tourId(),
                            assignment.targetId());
                    log.info("==> [Activity Cruise] Xử lý assignment thành công cho Tour ID: {}", assignment.tourId());
                } catch (Exception e) {
                    log.error("==> [Activity Cruise] Lỗi xử lý assignment", e);
                    throw e;
                }
            }
        }
    }
}