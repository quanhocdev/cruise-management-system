package com.project.convenience.listener;

import com.project.common.event.TourApprovedEvent;
import com.project.common.event.TourAssignmentEvent;
import com.project.convenience.service.service.ServiceTourService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class ServiceTourAssignmentListener {

    private static final Logger log = LoggerFactory.getLogger(ServiceTourAssignmentListener.class);

    private final ServiceTourService serviceTourService;

    public ServiceTourAssignmentListener(
            ServiceTourService serviceTourService) {

        this.serviceTourService = serviceTourService;
    }

    // =========================================================
    // LISTEN TOUR APPROVED
    // =========================================================

    @KafkaListener(topics = "tour-approved-topic", groupId = "service-cruise-group-v1", containerFactory = "serviceKafkaListenerContainerFactory")
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

            // Chỉ Service xử lý SERVICE
            if (!"SERVICE".equalsIgnoreCase(
                    assignment.areaType())) {

                continue;
            }

            log.info(
                    "==> [Service] Tour ID: {}, Cruise Area ID: {}, Area Type: {}",
                    assignment.tourId(),
                    assignment.cruiseAreaId(),
                    assignment.areaType());

            try {

                serviceTourService.createServiceTourFromEvent(
                        assignment.tourId(),
                        assignment.cruiseAreaId());

                log.info(
                        "==> [Service] Tạo ServiceTour thành công - " +
                                "Tour ID: {}, Cruise Area ID: {}",
                        assignment.tourId(),
                        assignment.cruiseAreaId());

            } catch (Exception e) {

                log.error(
                        "==> [Service] Lỗi xử lý ServiceTour - " +
                                "Tour ID: {}, Cruise Area ID: {}",
                        assignment.tourId(),
                        assignment.cruiseAreaId(),
                        e);

                throw e;
            }
        }
    }
}