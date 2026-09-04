package com.project.convenience.listener;

import com.project.common.event.TourApprovedEvent;
import com.project.common.event.TourAssignmentEvent;
import com.project.common.event.enums.TourAssignmentType;
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

        @KafkaListener(topics = "tour-approved-topic", groupId = "product-cruise-group-v1")
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
                        // CHỈ PRODUCT XỬ LÝ PRODUCT
                        // =================================================

                        if (assignment.type() != TourAssignmentType.PRODUCT) {
                                continue;
                        }

                        log.info(
                                        "==> [Product] Tour ID: {}, Target ID: {}, Type: {}",
                                        assignment.tourId(),
                                        assignment.targetId(),
                                        assignment.type());

                        // =================================================
                        // TẠO PRODUCT TOUR
                        // =================================================

                        try {

                                productTourService.createProductTourFromEvent(
                                                assignment.tourId(),
                                                assignment.targetId());

                                log.info(
                                                "==> [Product] Tạo ProductTour thành công - " +
                                                                "Tour ID: {}, Target ID: {}",
                                                assignment.tourId(),
                                                assignment.targetId());

                        } catch (Exception e) {

                                log.error(
                                                "==> [Product] Lỗi xử lý ProductTour - " +
                                                                "Tour ID: {}, Target ID: {}",
                                                assignment.tourId(),
                                                assignment.targetId(),
                                                e);

                                throw e;
                        }
                }
        }
}