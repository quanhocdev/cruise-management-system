package com.project.tour.service.activitycruise;

import com.project.tour.dto.activitycruise.ActivityCruiseTourResponse;
import com.project.tour.dto.activitycruise.HistoryActivityCruiseTourResponse;
import com.project.tour.mapper.activitycruise.ActivityCruiseTourMapper;
import com.project.tour.mapper.activitycruise.HistoryActivityCruiseTourMapper;
import com.project.tour.model.activitycruise.ActivityCruiseTour;
import com.project.tour.model.activitycruise.enums.ActivityCruiseTourStatus;
import com.project.tour.repository.activitycruise.ActivityCruiseTourAssignmentRepository;
import com.project.tour.repository.activitycruise.HistoryActivityCruiseTourRepository;

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
    // TẠO ACTIVITY CRUISE TOUR ASSIGNMENT
    // =====================================================

    public void createActivityCruiseTour(
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
    // XÓA ACTIVITY CRUISE TOUR ASSIGNMENT
    // =====================================================

    public void deleteActivityCruiseTour(
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

    // =====================================================
    // GET ALL ASSIGNMENTS
    // =====================================================

    @Transactional(readOnly = true)
    public List<ActivityCruiseTourResponse> getAllAssignments() {

        return assignmentRepository
                .findAll()
                .stream()
                .map(activityCruiseTourMapper::toResponse)
                .toList();
    }

    // =====================================================
    // GET CONFIGURATION HISTORY
    // =====================================================

    @Transactional(readOnly = true)
    public List<HistoryActivityCruiseTourResponse> getConfigurationHistory() {

        return historyRepository
                .findAllByOrderByCompletedAtDesc()
                .stream()
                .map(historyMapper::toResponse)
                .toList();
    }

    // =====================================================
    // GET CONFIGURATION DETAIL BY TOUR
    // =====================================================

    @Transactional(readOnly = true)
    public List<ActivityCruiseTourResponse> getConfigurationDetail(
            UUID tourId) {

        return assignmentRepository
                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                .stream()
                .map(activityCruiseTourMapper::toResponse)
                .toList();
    }
}
