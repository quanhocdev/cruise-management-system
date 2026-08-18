package com.project.tour.repository.tour;

import com.project.tour.model.ActivityCruiseTour;
import com.project.tour.model.enums.onboard.ActivityCruiseTourStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActivityCruiseTourAssignmentRepository
                extends JpaRepository<ActivityCruiseTour, UUID> {

        List<ActivityCruiseTour> findAllByTourIdOrderByCreatedAtAsc(UUID tourId);

        List<ActivityCruiseTour> findAllByStatusOrderByCreatedAtAsc(ActivityCruiseTourStatus status);

        List<ActivityCruiseTour> findAllByTourIdAndStatusOrderByCreatedAtAsc(
                        UUID tourId, ActivityCruiseTourStatus status);

        // Kiểm tra đã phân công khu vực này cho tour chưa
        Optional<ActivityCruiseTour> findByTourIdAndCruiseAreaId(UUID tourId, UUID cruiseAreaId);

        // Xóa phân công theo tourId và cruiseAreaId
        void deleteByTourIdAndCruiseAreaId(UUID tourId, UUID cruiseAreaId);
}