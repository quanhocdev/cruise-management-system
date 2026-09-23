package com.project.tour.service.passenger;

import com.project.tour.model.activitycruise.ActivityCruiseTour;
import com.project.tour.model.activityvisit.VisitTour;
import com.project.tour.repository.activitycruise.ActivityCruiseTourAssignmentRepository;
import com.project.tour.repository.activityvisit.VisitTourRepository;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.repository.tour.schedule.ScheduleStopRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PassengerTripActivities {

    private final ActivityCruiseTourAssignmentRepository onboard;
    private final VisitTourRepository shore;
    private final CruiseAreaRepository areas;
    private final ScheduleStopRepository stops;

    private static final Set<String> VISIBLE = Set.of(
            "CONFIGURED",
            "NOT_STARTED",
            "IN_PROGRESS",
            "COMPLETED",
            "DELAYED",
            "CANCELLED");

    public PassengerTripActivities(
            ActivityCruiseTourAssignmentRepository onboard,
            VisitTourRepository shore,
            CruiseAreaRepository areas,
            ScheduleStopRepository stops) {

        this.onboard = onboard;
        this.shore = shore;
        this.areas = areas;
        this.stops = stops;
    }

    public List<Activity> get(UUID tourId) {

        List<Activity> result = new ArrayList<>();

        // =====================================================
        // ONBOARD ACTIVITIES
        // =====================================================

        for (ActivityCruiseTour a : onboard.findAllByTourIdOrderByCreatedAtAsc(tourId)) {

            if (!visible(
                    a.getId(),
                    a.getStatus() != null ? a.getStatus().name() : null,
                    a.getActivityName())) {
                continue;
            }

            String location = a.getCruiseAreaId() == null
                    ? null
                    : areas.findById(a.getCruiseAreaId())
                            .map(area -> area.getName())
                            .orElse(null);

            result.add(new Activity(
                    a.getId(),
                    "ONBOARD",
                    a.getActivityName(),
                    a.getActivityDescription(),
                    a.getStartTime(),
                    a.getEndTime(),
                    location,
                    a.getPrice(),
                    a.getMaxPassengers(),
                    a.getStatus() != null ? a.getStatus().name() : null));
        }

        // =====================================================
        // SHORE ACTIVITIES
        // =====================================================

        for (VisitTour a : shore.findAllByTourIdOrderByStartTimeAsc(tourId)) {

            if (!visible(
                    a.getId(),
                    a.getStatus() != null ? a.getStatus().name() : null,
                    a.getName())) {
                continue;
            }

            String location = a.getScheduleStopId() == null
                    ? null
                    : stops.findById(a.getScheduleStopId())
                            .map(stop -> stop.getPort() == null
                                    ? null
                                    : stop.getPort().getName())
                            .orElse(null);

            result.add(new Activity(
                    a.getId(),
                    "SHORE",
                    a.getName(),
                    a.getDescription(),
                    a.getStartTime(),
                    a.getEndTime(),
                    location,
                    a.getPrice(),
                    a.getMaxPassengers(),
                    a.getStatus() != null ? a.getStatus().name() : null));
        }

        // =====================================================
        // SORT
        // =====================================================

        result.sort(
                Comparator.comparing(
                        Activity::startTime,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Activity::name));

        return result;
    }

    private boolean visible(
            UUID id,
            String status,
            String name) {

        return id != null
                && status != null
                && VISIBLE.contains(status)
                && name != null
                && !name.isBlank();
    }

    // These are synchronized descriptions,
    // not live registration/seat counts.
    public record Activity(
            UUID id,
            String type,
            String name,
            String description,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String location,
            BigDecimal price,
            Integer maxPassengers,
            String status) {
    }
}
