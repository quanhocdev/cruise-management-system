package com.project.activitycruise.repository;

import com.project.activitycruise.model.ActivityCruiseTour;
import com.project.activitycruise.model.enums.ActivityCruiseTourStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActivityCruiseTourAssignmentRepository extends JpaRepository<ActivityCruiseTour, UUID> {

        // Lấy danh sách ActivityCruiseTour theo tourId (Chỉ load kèm ActivityCruise)
        @EntityGraph(attributePaths = { "activityCruise" })
        List<ActivityCruiseTour> findAllByTourIdOrderByCreatedAtAsc(UUID tourId);

        // Lấy danh sách theo status
        @EntityGraph(attributePaths = { "activityCruise" })
        List<ActivityCruiseTour> findAllByStatusOrderByCreatedAtAsc(ActivityCruiseTourStatus status);

        // Lấy danh sách theo tourId và status
        @EntityGraph(attributePaths = { "activityCruise" })
        List<ActivityCruiseTour> findAllByTourIdAndStatusOrderByCreatedAtAsc(UUID tourId,
                        ActivityCruiseTourStatus status);

        // Kiểm tra/Tìm bản ghi theo tourId và cruiseAreaId
        Optional<ActivityCruiseTour> findByTourIdAndCruiseAreaId(UUID tourId, UUID cruiseAreaId);

        // Xóa theo tourId và cruiseAreaId
        void deleteByTourIdAndCruiseAreaId(UUID tourId, UUID cruiseAreaId);

        // Lấy danh sách ActivityCruiseTour theo trạng thái status (Chỉ join fetch
        // activityCruise)
        @Query("""
                        SELECT act
                        FROM ActivityCruiseTour act
                        LEFT JOIN FETCH act.activityCruise ac
                        WHERE act.status = :status
                        ORDER BY act.createdAt ASC
                        """)
        List<ActivityCruiseTour> findPendingConfig(@Param("status") ActivityCruiseTourStatus status);

        // Lấy danh sách theo nhiều trạng thái status
        @Query("""
                        SELECT act
                        FROM ActivityCruiseTour act
                        LEFT JOIN FETCH act.activityCruise ac
                        WHERE act.status IN :statuses
                        ORDER BY act.createdAt ASC
                        """)
        List<ActivityCruiseTour> findConfigurable(@Param("statuses") List<ActivityCruiseTourStatus> statuses);
}