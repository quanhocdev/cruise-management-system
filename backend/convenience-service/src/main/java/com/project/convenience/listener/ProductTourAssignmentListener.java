package com.project.convenience.listener;

import com.project.common.event.ProductTourAssignedEvent;
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

    private final ProductTourService productTourService; // Inject Service xử lý DB

    public ProductTourAssignmentListener(ProductTourService productTourService) {
        this.productTourService = productTourService;
    }

    @KafkaListener(topics = "tour-product-assignment-topic", groupId = "convenience-group", containerFactory = "kafkaListenerContainerFactory")
    public void onProductTourAssigned(
            ProductTourAssignedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key) {
        log.info("==> [Kafka Consumer] Nhận Event từ Partition: {}, Offset: {}, Key: {}", partition, offset, key);
        log.info("==> [Data] Tour ID: {}, Cruise Area ID: {}", event.tourId(), event.cruiseAreaId());

        // Gọi Service nghiệp vụ để lưu/cập nhật DB bên convenience-service
        try {
            productTourService.createProductTourFromEvent(event.tourId(), event.cruiseAreaId());

            log.info("==> [Kafka Consumer] Xử lý đồng bộ tiện ích thành công cho Tour ID: {}", event.tourId());
        } catch (Exception e) {
            log.error("==> [Kafka Consumer] Lỗi khi xử lý Event cho Tour ID: {}", event.tourId(), e);
            throw e;
        }
    }
}