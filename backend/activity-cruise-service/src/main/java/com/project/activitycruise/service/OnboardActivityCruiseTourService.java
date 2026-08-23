package com.project.activitycruise.service;

import com.project.activitycruise.dto.OnboardActivityCruiseTourResponse;
import com.project.activitycruise.mapper.ActivityCruiseTourMapper;
import com.project.activitycruise.model.enums.ActivityCruiseTourStatus;
import com.project.activitycruise.repository.ActivityCruiseTourAssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OnboardActivityCruiseTourService {

    private final ActivityCruiseTourAssignmentRepository assignmentRepository;
    private final ActivityCruiseTourMapper activityCruiseTourMapper;

    public OnboardActivityCruiseTourService(
            ActivityCruiseTourAssignmentRepository assignmentRepository,
            ActivityCruiseTourMapper activityCruiseTourMapper) {

        this.assignmentRepository = assignmentRepository;
        this.activityCruiseTourMapper = activityCruiseTourMapper;
    }

    /**
     * Lấy các ActivityCruiseTour mà ONBOARD cần cấu hình.
     *
     * Điều kiện:
     * ActivityCruiseTour = WAITING_CONFIG
     */
    public List<OnboardActivityCruiseTourResponse> getPendingConfig() {

        return assignmentRepository
                .findPendingConfig(ActivityCruiseTourStatus.WAITING_CONFIG)
                .stream()
                .map(activityCruiseTourMapper::toResponse)
                .toList();
    }
}