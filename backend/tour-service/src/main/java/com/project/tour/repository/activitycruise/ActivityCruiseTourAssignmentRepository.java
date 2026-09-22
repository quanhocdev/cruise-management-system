package com.project.tour.repository.activitycruise;

import com.project.tour.model.activitycruise.ActivityCruiseTour;
import com.project.tour.model.activitycruise.enums.ActivityCruiseTourStatus;
import com.project.tour.model.enums.tour.TourStatusTrip;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActivityCruiseTourAssignmentRepository
                extends JpaRepository<ActivityCruiseTour, UUID> {

        // =====================================================
        // LẤY DANH SÁCH THEO TOUR
        // =====================================================

        @EntityGraph(attributePaths = { "activityCruise" })
        List<ActivityCruiseTour> findAllByTourId(
                        UUID tourId);

        @EntityGraph(attributePaths = { "activityCruise" })
        List<ActivityCruiseTour> findAllByTourIdOrderByCreatedAtAsc(
                        UUID tourId);

        // =====================================================
        // LẤY TẤT CẢ
        // =====================================================

        @EntityGraph(attributePaths = { "activityCruise" })
        List<ActivityCruiseTour> findAllByOrderByCreatedAtAsc();

        // =====================================================
        // LẤY THEO STATUS
        // =====================================================

        @EntityGraph(attributePaths = { "activityCruise" })
        List<ActivityCruiseTour> findAllByStatusOrderByCreatedAtAsc(
                        ActivityCruiseTourStatus status);

        @EntityGraph(attributePaths = { "activityCruise" })
        List<ActivityCruiseTour> findAllByTourIdAndStatusOrderByCreatedAtAsc(
                        UUID tourId,
                        ActivityCruiseTourStatus status);

        // =====================================================
        // TÌM THEO TOUR + CRUISE AREA
        // =====================================================

        Optional<ActivityCruiseTour> findByTourIdAndCruiseAreaId(
                        UUID tourId,
                        UUID cruiseAreaId);

        boolean existsByTourIdAndCruiseAreaId(
                        UUID tourId,
                        UUID cruiseAreaId);

        // =====================================================
        // XÓA THEO TOUR + CRUISE AREA
        // =====================================================

        @Modifying
        @Query("""
                        DELETE FROM ActivityCruiseTour a
                        WHERE a.tourId = :tourId
                          AND a.cruiseAreaId = :cruiseAreaId
                        """)
        void deleteByTourIdAndCruiseAreaId(
                        @Param("tourId") UUID tourId,
                        @Param("cruiseAreaId") UUID cruiseAreaId);

        // =====================================================
        // ACTIVITY CRUISE ĐANG CHỜ CẤU HÌNH
        // Chỉ lấy các assignment thuộc Tour đã APPROVED
        // =====================================================

        @Query("""
                        SELECT act
                        FROM ActivityCruiseTour act
                        JOIN Tour tour
                          ON tour.id = act.tourId
                        LEFT JOIN FETCH act.activityCruise ac
                        WHERE act.status = :status
                          AND tour.statusTrip = :tourStatus
                        ORDER BY act.createdAt ASC
                        """)
        List<ActivityCruiseTour> findPendingConfigForApprovedTours(
                        @Param("status") ActivityCruiseTourStatus status,
                        @Param("tourStatus") TourStatusTrip tourStatus);

        // =====================================================
        // ACTIVITY CRUISE ĐANG CHỜ CẤU HÌNH
        // Hàm cũ - giữ lại nếu nơi khác vẫn đang sử dụng
        // =====================================================

        @Query("""
                        SELECT act
                        FROM ActivityCruiseTour act
                        LEFT JOIN FETCH act.activityCruise ac
                        WHERE act.status = :status
                        ORDER BY act.createdAt ASC
                        """)
        List<ActivityCruiseTour> findPendingConfig(
                        @Param("status") ActivityCruiseTourStatus status);

        // =====================================================
        // ACTIVITY CRUISE THEO NHIỀU STATUS
        // =====================================================

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
