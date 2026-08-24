package com.project.activitycruise.listener;

import com.project.common.event.TourApprovedEvent;
import com.project.common.event.TourAssignmentEvent;
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

    public ActivityCruiseTourAssignmentListener(
            ActivityCruiseTourService activityCruiseTourService) {

        this.activityCruiseTourService = activityCruiseTourService;
    }

    // =========================================================
    // LISTEN TOUR APPROVED
    // =========================================================

    @KafkaListener(topics = "tour-approved-topic", groupId = "activity-cruise-group-v1", containerFactory = "activityKafkaListenerContainerFactory")
    public void onTourApproved(
            TourApprovedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key) {

        log.info(
                "==> [Kafka Consumer] Nhận TourApprovedEvent - " +
                        "Partition: {}, Offset: {}, Tour ID: {}",
                partition,
                offset,
                event.tourId());

        if (event.assignments() == null ||
                event.assignments().isEmpty()) {

            log.info(
                    "==> [Kafka Consumer] Tour {} không có assignment",
                    event.tourId());

            return;
        }

        // =====================================================
        // DUYỆT CÁC ASSIGNMENT
        // =====================================================

        for (TourAssignmentEvent assignment : event.assignments()) {

            // Chỉ Activity Cruise xử lý ACTIVITY
            if (!"ACTIVITY".equalsIgnoreCase(
                    assignment.areaType())) {

                continue;
            }

            log.info(
                    "==> [Activity] Tour ID: {}, Cruise Area ID: {}, Area Type: {}",
                    assignment.tourId(),
                    assignment.cruiseAreaId(),
                    assignment.areaType());

            try {

                activityCruiseTourService.createActivityTourFromEvent(
                        assignment.tourId(),
                        assignment.cruiseAreaId());

                log.info(
                        "==> [Activity] Tạo ActivityCruiseTour thành công - " +
                                "Tour ID: {}, Cruise Area ID: {}",
                        assignment.tourId(),
                        assignment.cruiseAreaId());

            } catch (Exception e) {

                log.error(
                        "==> [Activity] Lỗi xử lý ActivityCruiseTour - " +
                                "Tour ID: {}, Cruise Area ID: {}",
                        assignment.tourId(),
                        assignment.cruiseAreaId(),
                        e);

                throw e;
            }
        }
    }
}