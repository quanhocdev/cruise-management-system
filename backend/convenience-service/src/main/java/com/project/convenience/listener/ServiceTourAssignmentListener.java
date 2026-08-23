package com.project.convenience.listener;

import com.project.common.event.TourAssignedEvent;
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

    public ServiceTourAssignmentListener(ServiceTourService serviceTourService) {
        this.serviceTourService = serviceTourService;
    }

    @KafkaListener(topics = "tour-service-assignment-topic", groupId = "convenience-group", containerFactory = "serviceKafkaListenerContainerFactory")
    public void onServiceTourAssigned(
            TourAssignedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key) {
        log.info("==> [Kafka Consumer] Nhận Event Service từ Partition: {}, Offset: {}, Action: {}", partition, offset,
                event.action());
        log.info("==> [Data] Tour ID: {}, Cruise Area ID: {}", event.tourId(), event.cruiseAreaId());

        try {
            if ("DELETE".equalsIgnoreCase(event.action())) {
                // Gọi hàm xóa trong Service của convenience-service
                serviceTourService.deleteServiceTourFromEvent(event.tourId(), event.cruiseAreaId());
                log.info("==> [Kafka Consumer] Xóa đồng bộ dịch vụ thành công cho Tour ID: {}", event.tourId());
            } else {
                // Mặc định hoặc CREATE: Tạo mới bản ghi WAITING_CONFIG
                serviceTourService.createServiceTourFromEvent(event.tourId(), event.cruiseAreaId());
                log.info("==> [Kafka Consumer] Xử lý đồng bộ dịch vụ thành công cho Tour ID: {}", event.tourId());
            }
        } catch (Exception e) {
            log.error("==> [Kafka Consumer] Lỗi khi xử lý Event Service cho Tour ID: {}", event.tourId(), e);
            throw e;
        }
    }
}