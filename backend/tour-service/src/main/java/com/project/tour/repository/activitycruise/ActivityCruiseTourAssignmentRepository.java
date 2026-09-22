package com.project.tour.repository.activitycruise;

import com.project.tour.model.activitycruise.ActivityCruiseTour;
import com.project.tour.model.activitycruise.enums.ActivityCruiseTourStatus;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActivityCruiseTourAssignmentRepository
        extends JpaRepository<ActivityCruiseTour, UUID> {

    // Lấy danh sách ActivityCruiseTour theo tourId
    // và load kèm ActivityCruise
    @EntityGraph(attributePaths = { "activityCruise" })
    List<ActivityCruiseTour> findAllByTourIdOrderByCreatedAtAsc(UUID tourId);

    // Lấy danh sách theo status
    @EntityGraph(attributePaths = { "activityCruise" })
    List<ActivityCruiseTour> findAllByStatusOrderByCreatedAtAsc(
            ActivityCruiseTourStatus status);

    // Lấy danh sách theo tourId và status
    @EntityGraph(attributePaths = { "activityCruise" })
    List<ActivityCruiseTour> findAllByTourIdAndStatusOrderByCreatedAtAsc(
            UUID tourId,
            ActivityCruiseTourStatus status);

    // Tìm bản ghi theo tourId và cruiseAreaId
    Optional<ActivityCruiseTour> findByTourIdAndCruiseAreaId(
            UUID tourId,
            UUID cruiseAreaId);

    // Xóa theo tourId và cruiseAreaId
    void deleteByTourIdAndCruiseAreaId(
            UUID tourId,
            UUID cruiseAreaId);

    // Lấy danh sách đang chờ cấu hình
    @Query("""
            SELECT act
            FROM ActivityCruiseTour act
            LEFT JOIN FETCH act.activityCruise ac
            WHERE act.status = :status
            ORDER BY act.createdAt ASC
            """)
    List<ActivityCruiseTour> findPendingConfig(
            @Param("status") ActivityCruiseTourStatus status);

    // Lấy danh sách theo nhiều trạng thái
    @Query("""
            SELECT act
            FROM ActivityCruiseTour act
            LEFT JOIN FETCH act.activityCruise ac
            WHERE act.status IN :statuses
            ORDER BY act.createdAt ASC
            """)
    List<ActivityCruiseTour> findConfigurable(
            @Param("statuses") List<ActivityCruiseTourStatus> statuses);
}
