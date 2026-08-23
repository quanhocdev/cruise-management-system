package com.project.activitycruise.listener;

import com.project.common.event.TourAssignedEvent;
import com.project.activitycruise.service.ActivityCruiseTourService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class ActivityCruiseTourAssignmentListener {

    private static final Logger log = LoggerFactory.getLogger(ActivityCruiseTourAssignmentListener.class);

    private final ActivityCruiseTourService activityCruiseTourService;

    public ActivityCruiseTourAssignmentListener(ActivityCruiseTourService activityCruiseTourService) {
        this.activityCruiseTourService = activityCruiseTourService;
    }

    @KafkaListener(topics = "tour-activity-assignment-topic", groupId = "activity-cruise-group-v1", containerFactory = "activityKafkaListenerContainerFactory")
    public void onActivityTourAssigned(
            TourAssignedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key) {

        log.info("==> [Kafka Consumer] Nhận Event Activity từ Partition: {}, Offset: {}, Action: {}",
                partition, offset, event.action());
        log.info("==> [Data] Tour ID: {}, Cruise Area ID: {}", event.tourId(), event.cruiseAreaId());

        try {
            if ("DELETE".equalsIgnoreCase(event.action())) {
                // Gọi hàm xóa trong Service của activity-cruise-service
                activityCruiseTourService.deleteActivityTourFromEvent(event.tourId(), event.cruiseAreaId());
                log.info("==> [Kafka Consumer] Xóa đồng bộ Activity thành công cho Tour ID: {}", event.tourId());
            } else {
                // Mặc định hoặc CREATE: Tạo/Cập nhật bản ghi phân công
                activityCruiseTourService.createActivityTourFromEvent(event.tourId(), event.cruiseAreaId());
                log.info("==> [Kafka Consumer] Xử lý đồng bộ Activity thành công cho Tour ID: {}", event.tourId());
            }
        } catch (Exception e) {
            log.error("==> [Kafka Consumer] Lỗi khi xử lý Event Activity cho Tour ID: {}", event.tourId(), e);
            throw e;
        }
    }
}