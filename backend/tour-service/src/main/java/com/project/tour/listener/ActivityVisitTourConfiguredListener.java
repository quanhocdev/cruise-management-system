package com.project.tour.listener;

import com.project.common.event.VisitTourConfiguredEvent;
import com.project.tour.service.tour.operation.OperationActivityVisitTourService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ActivityVisitTourConfiguredListener {

    private final OperationActivityVisitTourService activityVisitTourService;

    public ActivityVisitTourConfiguredListener(
            OperationActivityVisitTourService activityVisitTourService) {

        this.activityVisitTourService = activityVisitTourService;
    }

    @KafkaListener(topics = "visit-tour-configured-topic", groupId = "tour-service-activity-visit-group")
    public void handle(VisitTourConfiguredEvent event) {

        activityVisitTourService
                .handleVisitTourConfigured(event);
    }
}