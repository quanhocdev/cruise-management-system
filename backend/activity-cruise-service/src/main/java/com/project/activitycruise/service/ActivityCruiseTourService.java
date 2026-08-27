package com.project.activitycruise.service;

import com.project.activitycruise.dto.ActivityCruiseTourResponse;
import com.project.activitycruise.dto.HistoryActivityCruiseTourResponse;
import com.project.activitycruise.mapper.ActivityCruiseTourMapper;
import com.project.activitycruise.mapper.HistoryActivityCruiseTourMapper;
import com.project.activitycruise.model.ActivityCruiseTour;
import com.project.activitycruise.model.HistoryActivityCruiseTour;
import com.project.activitycruise.model.enums.ActivityCruiseTourStatus;
import com.project.activitycruise.repository.ActivityCruiseTourAssignmentRepository;
import com.project.activitycruise.repository.HistoryActivityCruiseTourRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ActivityCruiseTourService {

        private final ActivityCruiseTourAssignmentRepository assignmentRepository;
        private final ActivityCruiseTourMapper activityCruiseTourMapper;
        private final HistoryActivityCruiseTourRepository historyRepository;
        private final HistoryActivityCruiseTourMapper historyMapper;

        public ActivityCruiseTourService(
                        ActivityCruiseTourAssignmentRepository assignmentRepository,
                        ActivityCruiseTourMapper activityCruiseTourMapper,
                        HistoryActivityCruiseTourRepository historyRepository,
                        HistoryActivityCruiseTourMapper historyMapper) {

                this.assignmentRepository = assignmentRepository;
                this.activityCruiseTourMapper = activityCruiseTourMapper;
                this.historyRepository = historyRepository;
                this.historyMapper = historyMapper;
        }

        // =====================================================
        // Xử lý Event CREATE từ Kafka
        // =====================================================

        public void createActivityTourFromEvent(
                        UUID tourId,
                        UUID cruiseAreaId) {

                boolean exists = assignmentRepository
                                .findByTourIdAndCruiseAreaId(
                                                tourId,
                                                cruiseAreaId)
                                .isPresent();

                if (exists) {
                        return;
                }

                ActivityCruiseTour activityTour = new ActivityCruiseTour();

                activityTour.setTourId(tourId);
                activityTour.setCruiseAreaId(cruiseAreaId);
                activityTour.setStatus(
                                ActivityCruiseTourStatus.WAITING_CONFIG);

                assignmentRepository.save(activityTour);
        }

        // =====================================================
        // Xử lý Event DELETE từ Kafka
        // =====================================================

        public void deleteActivityTourFromEvent(
                        UUID tourId,
                        UUID cruiseAreaId) {

                assignmentRepository
                                .findByTourIdAndCruiseAreaId(
                                                tourId,
                                                cruiseAreaId)
                                .ifPresent(assignmentRepository::delete);
        }

        // =====================================================
        // GET ACTIVITIES ĐANG CHỜ CẤU HÌNH
        // =====================================================

        @Transactional(readOnly = true)
        public List<ActivityCruiseTourResponse> getPendingConfig() {

                return assignmentRepository
                                .findPendingConfig(
                                                ActivityCruiseTourStatus.WAITING_CONFIG)
                                .stream()
                                .map(activityCruiseTourMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<ActivityCruiseTourResponse> getAllAssignments() {

                return assignmentRepository
                                .findAll()
                                .stream()
                                .map(activityCruiseTourMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<HistoryActivityCruiseTourResponse> getConfigurationHistory() {

                return historyRepository
                                .findAllByOrderByCompletedAtDesc()
                                .stream()
                                .map(historyMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<ActivityCruiseTourResponse> getConfigurationDetail(UUID tourId) {

                return assignmentRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                                .stream()
                                .map(activityCruiseTourMapper::toResponse)
                                .toList();
        }
}