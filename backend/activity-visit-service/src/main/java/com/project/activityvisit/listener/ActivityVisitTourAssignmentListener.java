package com.project.activityvisit.listener;

import com.project.activityvisit.service.VisitTourService;
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
public class ActivityVisitTourAssignmentListener {

        private static final Logger log = LoggerFactory.getLogger(
                        ActivityVisitTourAssignmentListener.class);

        private final VisitTourService visitTourService;

        public ActivityVisitTourAssignmentListener(
                        VisitTourService visitTourService) {

                this.visitTourService = visitTourService;
        }

        // =========================================================
        // LISTEN TOUR APPROVED
        // =========================================================

        @KafkaListener(topics = "tour-approved-topic", groupId = "activity-visit-group-v1")
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

                        // Chỉ Activity Visit xử lý ACTIVITY_VISIT
                        if (assignment.type() != TourAssignmentType.ACTIVITY_VISIT) {

                                continue;
                        }

                        log.info(
                                        "==> [Activity Visit] " +
                                                        "Tour ID: {}, Schedule Stop ID: {}, Type: {}",
                                        assignment.tourId(),
                                        assignment.targetId(),
                                        assignment.type());

                        try {

                                visitTourService.createVisitTourFromEvent(
                                                assignment.tourId(),
                                                assignment.targetId());

                                log.info(
                                                "==> [Activity Visit] " +
                                                                "Tạo VisitTour thành công - " +
                                                                "Tour ID: {}, Schedule Stop ID: {}",
                                                assignment.tourId(),
                                                assignment.targetId());

                        } catch (Exception e) {

                                log.error(
                                                "==> [Activity Visit] " +
                                                                "Lỗi xử lý VisitTour - " +
                                                                "Tour ID: {}, Schedule Stop ID: {}",
                                                assignment.tourId(),
                                                assignment.targetId(),
                                                e);

                                throw e;
                        }
                }
        }
}