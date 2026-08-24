package com.project.activitycruise.service;

import com.project.activitycruise.dto.OnboardActivityCruiseTourResponse;
import com.project.activitycruise.mapper.ActivityCruiseTourMapper;
import com.project.activitycruise.model.ActivityCruiseTour;
import com.project.activitycruise.model.enums.ActivityCruiseTourStatus;
import com.project.activitycruise.repository.ActivityCruiseTourAssignmentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ActivityCruiseTourService {

    private final ActivityCruiseTourAssignmentRepository assignmentRepository;
    private final ActivityCruiseTourMapper activityCruiseTourMapper;

    public ActivityCruiseTourService(
            ActivityCruiseTourAssignmentRepository assignmentRepository,
            ActivityCruiseTourMapper activityCruiseTourMapper) {

        this.assignmentRepository = assignmentRepository;
        this.activityCruiseTourMapper = activityCruiseTourMapper;
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
    public List<OnboardActivityCruiseTourResponse> getPendingConfig() {

        return assignmentRepository
                .findPendingConfig(
                        ActivityCruiseTourStatus.WAITING_CONFIG)
                .stream()
                .map(activityCruiseTourMapper::toResponse)
                .toList();
    }
}