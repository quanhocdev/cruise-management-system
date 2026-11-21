
package com.project.tour.service.passenger;

import com.project.tour.dto.passenger.activity.PassengerActivityCruiseDTO;
import com.project.tour.mapper.passenger.ActivityCruiseTourMapper;
import com.project.tour.model.onboard.ActivityCruiseTour;
import com.project.tour.model.onboard.enums.ActivityCruiseTourStatus;
import com.project.tour.repository.onboard.ActivityCruiseTourRepository;
import com.project.tour.repository.scheduler.TourRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PassengerActivityCruiseService {

    private static final Set<ActivityCruiseTourStatus> PASSENGER_VISIBLE_STATUSES = Set.of(
            ActivityCruiseTourStatus.CONFIGURED,
            ActivityCruiseTourStatus.NOT_STARTED,
            ActivityCruiseTourStatus.IN_PROGRESS,
            ActivityCruiseTourStatus.COMPLETED);

    private final TourRepository tourRepository;
    private final ActivityCruiseTourRepository activityCruiseTourRepository;
    private final ActivityCruiseTourMapper activityCruiseTourMapper;

    public PassengerActivityCruiseService(
            TourRepository tourRepository,
            ActivityCruiseTourRepository activityCruiseTourRepository,
            ActivityCruiseTourMapper activityCruiseTourMapper) {
        this.tourRepository = tourRepository;
        this.activityCruiseTourRepository = activityCruiseTourRepository;
        this.activityCruiseTourMapper = activityCruiseTourMapper;
    }

    public List<PassengerActivityCruiseDTO> getActivities(UUID tourId) {

        if (!tourRepository.existsById(tourId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy tour");
        }

        List<ActivityCruiseTour> activities = activityCruiseTourRepository
                .findAllByTourIdOrderByCreatedAtAsc(tourId);

        return activities.stream()
                .filter(item -> PASSENGER_VISIBLE_STATUSES.contains(item.getStatus()))
                .map(activityCruiseTourMapper::toDTO)
                .toList();
    }
}
