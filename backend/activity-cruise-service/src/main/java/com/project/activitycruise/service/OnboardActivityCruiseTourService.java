package com.project.activitycruise.service;

import com.project.activitycruise.dto.onboard.OnboardActivityCruiseTourResponse;
import com.project.activitycruise.model.enums.ActivityCruiseTourStatus;
import com.project.tour.mapper.tour.onboard.OnboardActivityCruiseTourMapper;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.repository.tour.ActivityCruiseTourAssignmentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OnboardActivityCruiseTourService {

    private final ActivityCruiseTourAssignmentRepository assignmentRepository;

    public OnboardActivityCruiseTourService(
            ActivityCruiseTourAssignmentRepository assignmentRepository) {

        this.assignmentRepository = assignmentRepository;
    }

    /**
     * Lấy các ActivityCruiseTour mà ONBOARD cần cấu hình.
     *
     * Điều kiện:
     *
     * Tour = APPROVED
     * ActivityCruiseTour = WAITING_CONFIG
     */
    public List<OnboardActivityCruiseTourResponse> getPendingConfig() {

        return assignmentRepository
                .findAllByTour_StatusTripAndStatusOrderByCreatedAtAsc(
                        TourStatusTrip.APPROVED,
                        ActivityCruiseTourStatus.WAITING_CONFIG)
                .stream()
                .map(OnboardActivityCruiseTourMapper::toResponse)
                .toList();
    }
}