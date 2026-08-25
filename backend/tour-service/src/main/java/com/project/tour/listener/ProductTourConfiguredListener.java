package com.project.tour.listener;

import com.project.common.event.ProductTourConfiguredEvent;
import com.project.tour.service.tour.operation.OperationProductTourService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductTourConfiguredListener {

    private final OperationProductTourService productTourService;

    public ProductTourConfiguredListener(
            OperationProductTourService productTourService) {

        this.productTourService = productTourService;
    }

    @KafkaListener(topics = "product-tour-configured-topic", groupId = "tour-service-product-tour-group", containerFactory = "productTourConfiguredKafkaListenerContainerFactory")
    public void handle(ProductTourConfiguredEvent event) {

        productTourService.handleProductTourConfigured(event);
    }
}