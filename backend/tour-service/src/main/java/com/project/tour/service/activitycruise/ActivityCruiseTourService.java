package com.project.tour.service.activitycruise;

import com.project.tour.dto.activitycruise.ActivityCruiseTourResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.activitycruise.ActivityCruiseTourMapper;
import com.project.tour.model.activitycruise.ActivityCruiseTour;
import com.project.tour.model.activitycruise.enums.ActivityCruiseTourStatus;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.repository.activitycruise.ActivityCruiseTourAssignmentRepository;

import org.springframework.http.HttpStatus;
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

        // TẠO ACTIVITY CRUISE TOUR ASSIGNMENT
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

        // XÓA ACTIVITY CRUISE TOUR ASSIGNMENT
        public void deleteActivityCruiseTour(
                        UUID tourId,
                        UUID cruiseAreaId) {

                ActivityCruiseTour assignment = assignmentRepository
                                .findByTourIdAndCruiseAreaId(
                                                tourId,
                                                cruiseAreaId)
                                .orElseThrow(() -> new AppException(
                                                "Activity cruise tour assignment not found",
                                                HttpStatus.NOT_FOUND));

                if (assignment.getStatus() != ActivityCruiseTourStatus.WAITING_CONFIG) {

                        throw new AppException(
                                        "Activity cruise configuration has already been completed and cannot be deleted",
                                        HttpStatus.CONFLICT);
                }

                assignmentRepository.delete(assignment);
        }

        // GET ACTIVITIES ĐANG CHỜ CẤU HÌNH
        @Transactional(readOnly = true)
        public List<ActivityCruiseTourResponse> getPendingConfig() {

                return assignmentRepository
                                .findPendingConfigForApprovedTours(
                                                ActivityCruiseTourStatus.WAITING_CONFIG,
                                                TourStatusTrip.APPROVED)
                                .stream()
                                .map(activityCruiseTourMapper::toResponse)
                                .toList();
        }

        // GET ALL ASSIGNMENTS
        @Transactional(readOnly = true)
        public List<ActivityCruiseTourResponse> getAllAssignments() {

                return assignmentRepository
                                .findAll()
                                .stream()
                                .map(activityCruiseTourMapper::toResponse)
                                .toList();
        }

        // GET CONFIGURATION DETAIL BY TOUR
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
