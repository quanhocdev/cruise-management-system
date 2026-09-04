package com.project.activityvisit.service;

import com.project.activityvisit.dto.HistoryActivityVisitTourResponse;
import com.project.activityvisit.dto.VisitTourResponse;
import com.project.activityvisit.exception.AppException;
import com.project.activityvisit.mapper.HistoryActivityVisitTourMapper;
import com.project.activityvisit.mapper.VisitTourMapper;
import com.project.activityvisit.model.HistoryActivityVisitTour;
import com.project.activityvisit.model.VisitTour;
import com.project.activityvisit.model.enums.VisitTourStatus;
import com.project.activityvisit.repository.HistoryActivityVisitTourRepository;
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
    private final HistoryActivityVisitTourRepository historyRepository;
    private final HistoryActivityVisitTourMapper historyMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ActivityVisitTourConfigurationService(
            VisitTourRepository visitTourRepository,
            HistoryActivityVisitTourRepository historyRepository,
            HistoryActivityVisitTourMapper historyMapper,
            KafkaTemplate<String, Object> kafkaTemplate) {

        this.visitTourRepository = visitTourRepository;
        this.historyRepository = historyRepository;
        this.historyMapper = historyMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void complete(UUID tourId) {

        List<VisitTour> visitTours = visitTourRepository
                .findAllByTourIdOrderByStartTimeAsc(tourId);

        if (visitTours.isEmpty()) {
            throw new AppException(
                    "No visit tour configuration found for tour",
                    HttpStatus.NOT_FOUND);
        }

        // =====================================================
        // KIỂM TRA TOUR ĐÃ HOÀN THÀNH TRƯỚC ĐÓ CHƯA
        // =====================================================

        if (historyRepository.existsByTourId(tourId)) {
            throw new AppException(
                    "Visit tour configuration for this tour has already been completed",
                    HttpStatus.CONFLICT);
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

        // =====================================================
        // LƯU HISTORY
        // =====================================================

        HistoryActivityVisitTour history = new HistoryActivityVisitTour();

        history.setTourId(tourId);
        history.setTotalConfigurations(visitTours.size());

        historyRepository.save(history);

        // =====================================================
        // PUBLISH KAFKA
        // =====================================================

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

    @Transactional(readOnly = true)
    public List<HistoryActivityVisitTourResponse> getConfigurationHistory() {

        return historyRepository
                .findAllByOrderByCompletedAtDesc()
                .stream()
                .map(historyMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VisitTourResponse> getConfigurationHistoryDetail(UUID tourId) {

        List<VisitTour> visitTours = visitTourRepository
                .findAllByTourIdOrderByStartTimeAsc(tourId);

        if (visitTours.isEmpty()) {
            throw new AppException(
                    "No visit tour configuration found for tour",
                    HttpStatus.NOT_FOUND);
        }

        return visitTours.stream()
                .map(VisitTourMapper::toResponse)
                .toList();
    }
}