package com.project.activitycruise.service;

import com.project.activitycruise.exception.AppException;
import com.project.activitycruise.model.ActivityCruise;
import com.project.activitycruise.model.ActivityCruiseTour;
import com.project.activitycruise.model.enums.ActivityCruiseTourStatus;
import com.project.activitycruise.repository.ActivityCruiseRepository;
import com.project.activitycruise.repository.ActivityCruiseTourAssignmentRepository;
import com.project.common.event.ActivityCruiseTourConfiguredEvent;

import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ActivityCruiseTourConfigurationService {

    private static final String ACTIVITY_CRUISE_TOUR_CONFIGURED_TOPIC = "activity-cruise-tour-configured-topic";

    private final ActivityCruiseTourAssignmentRepository assignmentRepository;
    private final ActivityCruiseRepository activityCruiseRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ActivityCruiseTourConfigurationService(
            ActivityCruiseTourAssignmentRepository assignmentRepository,
            ActivityCruiseRepository activityCruiseRepository,
            KafkaTemplate<String, Object> kafkaTemplate) {

        this.assignmentRepository = assignmentRepository;
        this.activityCruiseRepository = activityCruiseRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Hoàn thành cấu hình ActivityCruiseTour.
     *
     * Chỉ khi người dùng bấm "Hoàn thành" mới
     * publish Kafka event sang tour-service.
     */
    public void complete(UUID assignmentId) {

        ActivityCruiseTour assignment = assignmentRepository
                .findById(assignmentId)
                .orElseThrow(() -> new AppException(
                        "Activity cruise tour assignment not found",
                        HttpStatus.NOT_FOUND));

        if (assignment.getStatus() != ActivityCruiseTourStatus.CONFIGURED) {
            throw new AppException(
                    "Activity cruise tour must be CONFIGURED before completing",
                    HttpStatus.BAD_REQUEST);
        }

        ActivityCruise activityCruise = assignment.getActivityCruise();

        if (activityCruise == null) {
            throw new AppException(
                    "Activity cruise has not been selected",
                    HttpStatus.BAD_REQUEST);
        }

        ActivityCruiseTourConfiguredEvent event = new ActivityCruiseTourConfiguredEvent(
                assignment.getId(),
                assignment.getTourId(),
                assignment.getCruiseAreaId(),
                activityCruise.getId(),
                activityCruise.getName(),
                activityCruise.getDescription(),
                assignment.getStartTime(),
                assignment.getEndTime(),
                assignment.getMaxPassengers(),
                assignment.getPrice(),
                activityCruise.getImageUrl(),
                assignment.getStatus().name());

        kafkaTemplate.send(
                ACTIVITY_CRUISE_TOUR_CONFIGURED_TOPIC,
                assignment.getTourId().toString(),
                event);
    }
}