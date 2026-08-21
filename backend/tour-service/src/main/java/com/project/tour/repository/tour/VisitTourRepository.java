package com.project.tour.repository.tour;

import com.project.tour.model.VisitTour;
import com.project.tour.model.enums.visit.VisitTourStatus;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VisitTourRepository
        extends JpaRepository<VisitTour, UUID> {

    @EntityGraph(attributePaths = {
            "scheduleStop",
            "scheduleStop.schedule",
            "scheduleStop.schedule.tour",
            "scheduleStop.port"
    })
    List<VisitTour> findAllByOrderByCreatedAtDesc();

    @Override
    @EntityGraph(attributePaths = {
            "scheduleStop",
            "scheduleStop.schedule",
            "scheduleStop.schedule.tour",
            "scheduleStop.port"
    })
    Optional<VisitTour> findById(UUID id);

    @EntityGraph(attributePaths = {
            "scheduleStop",
            "scheduleStop.schedule",
            "scheduleStop.schedule.tour",
            "scheduleStop.port"
    })
    List<VisitTour> findAllByScheduleStopIdOrderByStartTimeAsc(
            UUID scheduleStopId);

    @EntityGraph(attributePaths = {
            "scheduleStop",
            "scheduleStop.schedule",
            "scheduleStop.schedule.tour",
            "scheduleStop.port"
    })
    List<VisitTour> findAllByScheduleStopScheduleTourIdOrderByStartTimeAsc(
            UUID tourId);

    // =====================================================
    // SHORE CONFIGURATION
    // =====================================================

    @EntityGraph(attributePaths = {
            "scheduleStop",
            "scheduleStop.schedule",
            "scheduleStop.schedule.tour",
            "scheduleStop.port"
    })
    List<VisitTour> findAllByScheduleStop_IdInOrderByStartTimeAsc(
            Collection<UUID> scheduleStopIds);

    List<VisitTour> findAllByScheduleStop_IdInAndStatusOrderByStartTimeAsc(
            List<UUID> scheduleStopIds,
            VisitTourStatus status);
}