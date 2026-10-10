package com.project.tour.listener;

import com.project.common.event.ProductUsedEvent;
import com.project.tour.service.convenience.product.ProductTourService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductUsedEventListener {

    private final ProductTourService productTourService;

    public ProductUsedEventListener(ProductTourService productTourService) {
        this.productTourService = productTourService;
    }

    @KafkaListener(topics = "product-used-topic", groupId = "tour-product-used-group")
    public void handle(ProductUsedEvent event) {
        productTourService.consumeQuantity(
                event.productTourId(),
                event.quantity());
    }
}