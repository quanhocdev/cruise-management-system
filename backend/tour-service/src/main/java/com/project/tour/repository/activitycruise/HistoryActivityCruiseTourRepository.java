package com.project.tour.repository.activitycruise;

import com.project.tour.model.activitycruise.HistoryActivityCruiseTour;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistoryActivityCruiseTourRepository
        extends JpaRepository<HistoryActivityCruiseTour, UUID> {

    boolean existsByTourId(UUID tourId);

    List<HistoryActivityCruiseTour> findAllByOrderByCompletedAtDesc();
}
