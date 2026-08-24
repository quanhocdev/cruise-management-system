package com.project.activitycruise.listener;

import com.project.activitycruise.service.ActivityCruiseTourService;
import com.project.common.event.TourApprovedEvent;
import com.project.common.event.TourAssignmentEvent;
import com.project.common.event.enums.TourAssignmentType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class ActivityCruiseTourAssignmentListener {

        private static final Logger log = LoggerFactory.getLogger(
                        ActivityCruiseTourAssignmentListener.class);

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

                // =====================================================
                // KIỂM TRA ASSIGNMENT
                // =====================================================

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

                        // =================================================
                        // CHỈ ACTIVITY CRUISE XỬ LÝ
                        // =================================================

                        if (assignment.type() != TourAssignmentType.ACTIVITY_CRUISE) {
                                continue;
                        }

                        log.info(
                                        "==> [Activity Cruise] " +
                                                        "Tour ID: {}, Target ID: {}, Type: {}",
                                        assignment.tourId(),
                                        assignment.targetId(),
                                        assignment.type());

                        // =================================================
                        // TẠO ACTIVITY CRUISE TOUR
                        // =================================================

                        try {

                                activityCruiseTourService.createActivityTourFromEvent(
                                                assignment.tourId(),
                                                assignment.targetId());

                                log.info(
                                                "==> [Activity Cruise] " +
                                                                "Tạo ActivityCruiseTour thành công - " +
                                                                "Tour ID: {}, Target ID: {}",
                                                assignment.tourId(),
                                                assignment.targetId());

                        } catch (Exception e) {

                                log.error(
                                                "==> [Activity Cruise] " +
                                                                "Lỗi xử lý ActivityCruiseTour - " +
                                                                "Tour ID: {}, Target ID: {}",
                                                assignment.tourId(),
                                                assignment.targetId(),
                                                e);

                                throw e;
                        }
                }
        }
}