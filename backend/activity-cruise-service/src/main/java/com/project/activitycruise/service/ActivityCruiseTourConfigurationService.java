package com.project.activitycruise.service;

import com.project.activitycruise.exception.AppException;
import com.project.activitycruise.model.ActivityCruise;
import com.project.activitycruise.model.ActivityCruiseTour;
import com.project.activitycruise.model.HistoryActivityCruiseTour;
import com.project.activitycruise.model.enums.ActivityCruiseTourStatus;
import com.project.activitycruise.repository.ActivityCruiseTourAssignmentRepository;
import com.project.common.event.ActivityCruiseTourConfiguredEvent;
import com.project.activitycruise.repository.HistoryActivityCruiseTourRepository;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ActivityCruiseTourConfigurationService {

        private static final String ACTIVITY_CRUISE_TOUR_CONFIGURED_TOPIC = "activity-cruise-tour-configured-topic";

        private final ActivityCruiseTourAssignmentRepository assignmentRepository;
        private final HistoryActivityCruiseTourRepository historyRepository;
        private final KafkaTemplate<String, Object> kafkaTemplate;

        public ActivityCruiseTourConfigurationService(
                        ActivityCruiseTourAssignmentRepository assignmentRepository,
                        HistoryActivityCruiseTourRepository historyRepository,
                        KafkaTemplate<String, Object> kafkaTemplate) {

                this.assignmentRepository = assignmentRepository;
                this.historyRepository = historyRepository;
                this.kafkaTemplate = kafkaTemplate;
        }

        /**
         * Hoàn thành cấu hình tất cả ActivityCruiseTour của một Tour.
         *
         * Chỉ khi bấm "Hoàn thành" mới
         * publish Kafka event sang tour-service.
         *
         * Mỗi ActivityCruiseTour tương ứng với một Kafka message.
         */
        public void complete(UUID tourId) {

                // =====================================================
                // 1. KIỂM TRA ĐÃ HOÀN THÀNH CHƯA
                // =====================================================

                if (historyRepository.existsByTourId(tourId)) {

                        throw new AppException(
                                        "Activity cruise tour configuration has already been completed",
                                        HttpStatus.BAD_REQUEST);
                }

                // =====================================================
                // 2. LẤY CẤU HÌNH
                // =====================================================

                List<ActivityCruiseTour> assignments = assignmentRepository.findAllByTourIdOrderByCreatedAtAsc(tourId);

                if (assignments.isEmpty()) {

                        throw new AppException(
                                        "No activity cruise tour configuration found for tour",
                                        HttpStatus.NOT_FOUND);
                }

                // =====================================================
                // 3. KIỂM TRA TẤT CẢ CONFIGURED
                // =====================================================

                for (ActivityCruiseTour assignment : assignments) {

                        if (assignment.getStatus() != ActivityCruiseTourStatus.CONFIGURED) {

                                throw new AppException(
                                                "All activity cruise tours must be CONFIGURED before completing configuration",
                                                HttpStatus.BAD_REQUEST);
                        }

                        if (assignment.getActivityCruise() == null) {

                                throw new AppException(
                                                "Activity cruise configuration is missing",
                                                HttpStatus.BAD_REQUEST);
                        }
                }

                // =====================================================
                // 4. LƯU LỊCH SỬ HOÀN THÀNH
                // =====================================================

                HistoryActivityCruiseTour history = new HistoryActivityCruiseTour();

                history.setTourId(tourId);

                history.setTotalConfigurations(assignments.size());

                historyRepository.save(history);

                // =====================================================
                // 5. GỬI KAFKA
                // =====================================================

                for (ActivityCruiseTour assignment : assignments) {

                        ActivityCruise activityCruise = assignment.getActivityCruise();

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
}