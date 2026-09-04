package com.project.tour.listener;

import com.project.common.event.ActivityCruiseTourConfiguredEvent;
import com.project.tour.service.tour.operation.OperationActivityCruiseTourService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ActivityCruiseTourConfiguredListener {

    private final OperationActivityCruiseTourService activityCruiseTourService;

    public ActivityCruiseTourConfiguredListener(
            OperationActivityCruiseTourService activityCruiseTourService) {

        this.activityCruiseTourService = activityCruiseTourService;
    }

    @KafkaListener(topics = "activity-cruise-tour-configured-topic", groupId = "tour-service-activity-cruise-group")
    public void handle(ActivityCruiseTourConfiguredEvent event) {

        activityCruiseTourService
                .handleActivityCruiseTourConfigured(event);
    }
}