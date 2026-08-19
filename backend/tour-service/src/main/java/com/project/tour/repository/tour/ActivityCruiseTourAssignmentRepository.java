package com.project.tour.repository.tour;

import com.project.tour.model.ActivityCruiseTour;
import com.project.tour.model.enums.onboard.ActivityCruiseTourStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActivityCruiseTourAssignmentRepository
                extends JpaRepository<ActivityCruiseTour, UUID> {

        @EntityGraph(attributePaths = { "tour", "cruiseArea", "cruiseArea.cruiseDeck", "activityCruise" })
        List<ActivityCruiseTour> findAllByTourIdOrderByCreatedAtAsc(UUID tourId);

        @EntityGraph(attributePaths = { "tour", "cruiseArea", "cruiseArea.cruiseDeck", "activityCruise" })
        List<ActivityCruiseTour> findAllByStatusOrderByCreatedAtAsc(ActivityCruiseTourStatus status);

        @EntityGraph(attributePaths = { "tour", "cruiseArea", "cruiseArea.cruiseDeck", "activityCruise" })
        List<ActivityCruiseTour> findAllByTourIdAndStatusOrderByCreatedAtAsc(
                        UUID tourId, ActivityCruiseTourStatus status);

        // Kiểm tra đã phân công khu vực này cho tour chưa
        Optional<ActivityCruiseTour> findByTourIdAndCruiseAreaId(UUID tourId, UUID cruiseAreaId);

        // Xóa phân công theo tourId và cruiseAreaId
        void deleteByTourIdAndCruiseAreaId(UUID tourId, UUID cruiseAreaId);
}