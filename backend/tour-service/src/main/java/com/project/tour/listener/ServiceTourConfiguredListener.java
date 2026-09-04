package com.project.tour.listener;

import com.project.common.event.ServiceTourConfiguredEvent;
import com.project.tour.service.tour.operation.OperationServiceTourService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ServiceTourConfiguredListener {

    private final OperationServiceTourService serviceTourService;

    public ServiceTourConfiguredListener(
            OperationServiceTourService serviceTourService) {

        this.serviceTourService = serviceTourService;
    }

    @KafkaListener(topics = "service-tour-configured-topic", groupId = "tour-service-service-tour-group")
    public void handle(ServiceTourConfiguredEvent event) {

        serviceTourService.handleServiceTourConfigured(event);
    }
}