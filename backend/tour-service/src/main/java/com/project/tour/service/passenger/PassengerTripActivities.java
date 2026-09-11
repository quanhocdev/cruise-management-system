package com.project.tour.service.passenger;

import com.project.tour.repository.tour.AssignmentActivityCruiseRepository;
import com.project.tour.repository.tour.AssignmentActivityVisitRepository;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.repository.tour.schedule.ScheduleStopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class PassengerTripActivities {
    private final AssignmentActivityCruiseRepository onboard;
    private final AssignmentActivityVisitRepository shore;
    private final CruiseAreaRepository areas;
    private final ScheduleStopRepository stops;
    private static final Set<String> VISIBLE = Set.of("CONFIGURED", "NOT_STARTED", "IN_PROGRESS", "COMPLETED", "DELAYED", "CANCELLED");
    public PassengerTripActivities(AssignmentActivityCruiseRepository onboard, AssignmentActivityVisitRepository shore,
            CruiseAreaRepository areas, ScheduleStopRepository stops) {
        this.onboard = onboard; this.shore = shore; this.areas = areas; this.stops = stops;
    }
    public List<Activity> get(UUID tourId) {
        List<Activity> result = new ArrayList<>();
        for (var a : onboard.findAllByTourIdOrderByCreatedAtAsc(tourId)) {
            if (!visible(a.getActivityCruiseTourId(), a.getStatus(), a.getActivityName())) continue;
            String location = a.getCruiseAreaId() == null ? null : areas.findById(a.getCruiseAreaId())
                .map(area -> area.getName()).orElse(null);
            result.add(new Activity(a.getActivityCruiseTourId(), "ONBOARD", a.getActivityName(), a.getActivityDescription(),
                a.getStartTime(), a.getEndTime(), location, a.getPrice(), a.getMaxPassengers(), a.getStatus()));
        }
        for (var a : shore.findAllByTourIdOrderByCreatedAtAsc(tourId)) {
            if (!visible(a.getVisitTourId(), a.getStatus(), a.getVisitName())) continue;
            String location = a.getScheduleStopId() == null ? null : stops.findById(a.getScheduleStopId())
                .map(stop -> stop.getPort() == null ? null : stop.getPort().getName()).orElse(null);
            result.add(new Activity(a.getVisitTourId(), "SHORE", a.getVisitName(), a.getVisitDescription(),
                a.getStartTime(), a.getEndTime(), location, a.getPrice(), a.getMaxPassengers(), a.getStatus()));
        }
        result.sort(Comparator.comparing(Activity::startTime, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Activity::name));
        return result;
    }
    private boolean visible(UUID id, String status, String name) {
        return id != null && status != null && VISIBLE.contains(status) && name != null && !name.isBlank();
    }
    // These are synchronized descriptions, not live registration/seat counts.
    public record Activity(UUID id, String type, String name, String description, LocalDateTime startTime,
        LocalDateTime endTime, String location, BigDecimal price, Integer maxPassengers, String status) {}
}
