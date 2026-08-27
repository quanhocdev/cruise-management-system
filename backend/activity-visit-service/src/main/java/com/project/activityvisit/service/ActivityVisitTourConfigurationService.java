package com.project.activityvisit.service;

import com.project.activityvisit.exception.AppException;
import com.project.activityvisit.model.VisitTour;
import com.project.activityvisit.model.enums.VisitTourStatus;
import com.project.activityvisit.repository.VisitTourRepository;
import com.project.common.event.VisitTourConfiguredEvent;

import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ActivityVisitTourConfigurationService {

    private static final String VISIT_TOUR_CONFIGURED_TOPIC = "visit-tour-configured-topic";

    private final VisitTourRepository visitTourRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ActivityVisitTourConfigurationService(
            VisitTourRepository visitTourRepository,
            KafkaTemplate<String, Object> kafkaTemplate) {

        this.visitTourRepository = visitTourRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Hoàn thành cấu hình tất cả VisitTour của một Tour.
     *
     * Chỉ khi người dùng bấm "Hoàn thành" mới
     * publish Kafka event sang tour-service.
     *
     * Mỗi VisitTour tương ứng với một Kafka message.
     */
    public void complete(UUID tourId) {

        List<VisitTour> visitTours = visitTourRepository
                .findAllByTourIdOrderByStartTimeAsc(tourId);

        if (visitTours.isEmpty()) {
            throw new AppException(
                    "No visit tour configuration found for tour",
                    HttpStatus.NOT_FOUND);
        }

        // =====================================================
        // KIỂM TRA TẤT CẢ ĐÃ CONFIGURED
        // =====================================================

        for (VisitTour visitTour : visitTours) {

            if (visitTour.getStatus() != VisitTourStatus.CONFIGURED) {

                throw new AppException(
                        "All visit tours must be CONFIGURED before completing configuration",
                        HttpStatus.BAD_REQUEST);
            }
        }

        // từng VisitTour publish Kafka event sang tour-service
        for (VisitTour visitTour : visitTours) {

            VisitTourConfiguredEvent event = new VisitTourConfiguredEvent(
                    visitTour.getId(),
                    visitTour.getTourId(),
                    visitTour.getScheduleStopId(),
                    visitTour.getName(),
                    visitTour.getDescription(),
                    visitTour.getStartTime(),
                    visitTour.getEndTime(),
                    visitTour.getMaxPassengers(),
                    visitTour.getPrice(),
                    visitTour.getStatus().name());

            kafkaTemplate.send(
                    VISIT_TOUR_CONFIGURED_TOPIC,
                    visitTour.getTourId().toString(),
                    event);
        }
    }
}