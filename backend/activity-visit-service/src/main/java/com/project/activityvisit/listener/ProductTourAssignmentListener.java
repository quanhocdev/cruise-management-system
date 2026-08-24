package com.project.convenience.listener;

import com.project.common.event.TourApprovedEvent;
import com.project.common.event.TourAssignmentEvent;
import com.project.convenience.service.product.ProductTourService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class ProductTourAssignmentListener {

    private static final Logger log = LoggerFactory.getLogger(ProductTourAssignmentListener.class);

    private final ProductTourService productTourService;

    public ProductTourAssignmentListener(
            ProductTourService productTourService) {

        this.productTourService = productTourService;
    }

    // =========================================================
    // LISTEN TOUR APPROVED
    // =========================================================

    @KafkaListener(topics = "tour-approved-topic", groupId = "product-cruise-group-v1", containerFactory = "kafkaListenerContainerFactory")
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

            // Chỉ Product xử lý PRODUCT
            if (!"PRODUCT".equalsIgnoreCase(
                    assignment.areaType())) {

                continue;
            }

            log.info(
                    "==> [Product] Tour ID: {}, Cruise Area ID: {}, Area Type: {}",
                    assignment.tourId(),
                    assignment.cruiseAreaId(),
                    assignment.areaType());

            try {

                productTourService.createProductTourFromEvent(
                        assignment.tourId(),
                        assignment.cruiseAreaId());

                log.info(
                        "==> [Product] Tạo ProductTour thành công - " +
                                "Tour ID: {}, Cruise Area ID: {}",
                        assignment.tourId(),
                        assignment.cruiseAreaId());

            } catch (Exception e) {

                log.error(
                        "==> [Product] Lỗi xử lý ProductTour - " +
                                "Tour ID: {}, Cruise Area ID: {}",
                        assignment.tourId(),
                        assignment.cruiseAreaId(),
                        e);

                throw e;
            }
        }
    }
}