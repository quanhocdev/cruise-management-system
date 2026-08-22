package com.project.tour.repository.tour;

import com.project.tour.model.Tour;
import com.project.tour.model.enums.tour.TourBookingStatus;
import com.project.tour.model.enums.tour.TourStatusTrip;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TourRepository extends JpaRepository<Tour, UUID> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(
            String code,
            UUID excludedTourId);

    Optional<Tour> findByCodeIgnoreCase(String code);

    List<Tour> findAllByOrderByNameAsc();

    List<Tour> findAllByCruise_IdOrderByNameAsc(
            UUID cruiseId);

    List<Tour> findAllByStatusTripOrderByNameAsc(
            TourStatusTrip statusTrip);

    // =====================================================
    // SHORE
    // Lấy các Tour mà Shore được phép quản lý
    // =====================================================

    List<Tour> findAllByStatusTripInOrderByStartDateAsc(
            List<TourStatusTrip> statuses);

    List<Tour> findAllByStatusBookingOrderByNameAsc(
            TourBookingStatus statusBooking);

    List<Tour> findAllByCruise_IdAndStatusTripOrderByNameAsc(
            UUID cruiseId,
            TourStatusTrip statusTrip);

    Optional<Tour> findByIdAndStatusTripIn(
            UUID id,
            List<TourStatusTrip> statuses);

    @Query("""
                SELECT t
                FROM Tour t
                WHERE t.cruise.id = :cruiseId
                  AND t.statusTrip IN :statuses
                  AND t.startDate <= :endDate
                  AND t.endDate >= :startDate
            """)
    List<Tour> findConflictingTours(
            @Param("cruiseId") UUID cruiseId,
            @Param("statuses") List<TourStatusTrip> statuses,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}