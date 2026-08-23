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

    private final ProductTourService productTourService;

    public ProductTourAssignmentListener(ProductTourService productTourService) {
        this.productTourService = productTourService;
    }

    @KafkaListener(topics = "tour-product-assignment-topic", groupId = "convenience-group", containerFactory = "kafkaListenerContainerFactory")
    public void onProductTourAssigned(
            ProductTourAssignedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key) {
        log.info("==> [Kafka Consumer] Nhận Event Product từ Partition: {}, Offset: {}, Action: {}", partition, offset,
                event.action());
        log.info("==> [Data] Tour ID: {}, Cruise Area ID: {}", event.tourId(), event.cruiseAreaId());

        try {
            if ("DELETE".equalsIgnoreCase(event.action())) {
                // Gọi hàm xóa trong Service của convenience-service
                productTourService.deleteProductTourFromEvent(event.tourId(), event.cruiseAreaId());
                log.info("==> [Kafka Consumer] Xóa đồng bộ tiện ích thành công cho Tour ID: {}", event.tourId());
            } else {
                // Mặc định hoặc CREATE: Tạo/Cập nhật bản ghi
                productTourService.createProductTourFromEvent(event.tourId(), event.cruiseAreaId());
                log.info("==> [Kafka Consumer] Xử lý đồng bộ tiện ích thành công cho Tour ID: {}", event.tourId());
            }
        } catch (Exception e) {
            log.error("==> [Kafka Consumer] Lỗi khi xử lý Event Product cho Tour ID: {}", event.tourId(), e);
            throw e;
        }
    }
}